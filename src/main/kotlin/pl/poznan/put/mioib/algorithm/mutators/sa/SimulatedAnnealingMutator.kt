package pl.poznan.put.mioib.algorithm.mutators.sa

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.NeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.model.DeltaUpdate
import pl.poznan.put.mioib.model.SolutionProposal
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

// https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman
// TODO add adaptation
// https://www.researchgate.net/publication/227061666_Computing_the_Initial_Temperature_of_Simulated_Annealing
// https://www.scirp.org/pdf/AM_2017083014324828.pdf
class SimulatedAnnealingMutator(
        private val neighbourhoodBrowser: NeighbourhoodBrowser,
        private val random: Random,
        private val isBetter: SolutionValueComparator,
        private val increaseRatio: Double = 3.0,
        private val loopRatio: Double = 0.0
) : SolutionMutator {
    private lateinit var cooling: CoolingState

     // http://www.cs.put.poznan.pl/mkomosinski/lectures/optimization/SA.pdf?fbclid=IwAR3FWX0jjFsJogqjbgmAIYyJdsiTqeQnXNkD3euWkFIu05DK0bc55WCAO3Y"
    private fun initializeTemperature(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): Double {
        val deltaList = mutableListOf<Double>()

        for (i in 0..solution.sequence.size){
            val (update) = neighbourhoodBrowser.browse(solution.sequence, solutionEvaluator)
            deltaList.add(if (update.scoreDelta > 0) update.scoreDelta else 0.0)
        }

         return -deltaList.average() / ln(0.999)
    }

    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        var s = solution
        for (i in 0..(1 + s.sequence.size * loopRatio).toInt()) {
            update(s, solutionEvaluator)
            val (update) = neighbourhoodBrowser.browse(s.sequence, solutionEvaluator).ifEmpty { return solution }

            if (isBetter(0.0, update.scoreDelta) || shouldAccept(update)) {
                s = s updatedWith update
            }
        }
        return s
    }

    private fun update(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator) {
        if (!::cooling.isInitialized) {
            val coolingRatio = 0.9997
            cooling = CoolingState(
                    initializeTemperature(solution, solutionEvaluator),
                    coolingRatio,
                    max(solution.sequence.size * solution.sequence.size * 2, 1000),
                    increaseRatio
            )
        }
        cooling.update()
    }

    private fun shouldAccept(deltaUpdate: DeltaUpdate) =
            exp(-(if (deltaUpdate.scoreDelta > 0) deltaUpdate.scoreDelta else 0.0) / cooling.temperature) > random.nextDouble()

    override fun canMutate() = true
}

data class CoolingState(
        var temperature: Double,
        var coolingRatio: Double,
        var increaseEveryIterations: Int,
        var increaseRatio: Double
) {
    private var iteration = 0
    fun update() {
        temperature *= if (++iteration % increaseEveryIterations == 0)
            increaseRatio
        else
            coolingRatio
    }
}
