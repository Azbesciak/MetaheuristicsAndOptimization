package pl.poznan.put.mioib.algorithm.mutators.sa

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.NeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.model.DeltaUpdate
import pl.poznan.put.mioib.model.SolutionProposal
import kotlin.math.exp
import kotlin.math.max
import kotlin.random.Random

// https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman
// TODO add adaptation
// https://www.researchgate.net/publication/227061666_Computing_the_Initial_Temperature_of_Simulated_Annealing
// https://www.scirp.org/pdf/AM_2017083014324828.pdf
class SimulatedAnnealingMutator(
        private val neighbourhoodBrowser: NeighbourhoodBrowser,
        private val random: Random,
        private val isBetter: SolutionValueComparator
) : SolutionMutator {
    private lateinit var cooling: CoolingState
    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        update(solution)
        val (update) = neighbourhoodBrowser.browse(solution.sequence, solutionEvaluator).ifEmpty { return solution }

        if (isBetter(0.0, update.scoreDelta) || shouldAccept(update)) {
            return solution updatedWith update
        }
        return solution
    }

    private fun update(solution: SolutionProposal) {
        if (!::cooling.isInitialized)
            cooling = CoolingState(
                    solution.score / solution.sequence.size,
                    0.999,
                    max(solution.sequence.size * solution.sequence.size, 1000),
                    2.0
            )
        cooling.update()
    }

    // we assume that delta will be positive... kinda bad but will see
    private fun shouldAccept(deltaUpdate: DeltaUpdate) =
            exp(-deltaUpdate.scoreDelta / cooling.temperature) > random.nextDouble()

    override fun canMutate() = !::cooling.isInitialized || cooling.temperature > 0.001
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
