package pl.poznan.put.mioib.algorithm.mutators.ts

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.NeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.model.DeltaUpdate
import pl.poznan.put.mioib.model.SolutionProposal

class TabuSearchMutator(
        private val browser: NeighbourhoodBrowser,
        instanceSize: Int,
        private val tabuSize: Int,
        private val isBetter: SolutionValueComparator,
        worstPossibleValue: Double,
        private val breakTabuIfBetterThanTheBest: Boolean = true
) : SolutionMutator {
    private val tabuList = IntMatrix(instanceSize)
    private var iteration = 0
    private var neibourhoodPointer = 0
    private var currentNeighbourhood = emptyList<DeltaUpdate>()
    private var bestValueSoFar = worstPossibleValue
    private var worseUpdate = 0.0
    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        if (neibourhoodPointer > currentNeighbourhood.lastIndex) {
            currentNeighbourhood = browser.browse(solution.sequence, solutionEvaluator)
            neibourhoodPointer = 0
            worseUpdate = currentNeighbourhood.last().scoreDelta
        }
        val update = findUpdate(solutionEvaluator, solution) ?: forceTabuReevaluate(solutionEvaluator, solution)
        if (isBetter(update.scoreDelta, worseUpdate)) {
            neibourhoodPointer = Int.MAX_VALUE
        }
        tabuList[update.from, update.to] = ++iteration
        val solutionProposal = solution updatedWith update
        if (isBetter(bestValueSoFar, solutionProposal.score))
            bestValueSoFar = solutionProposal.score
        return solutionProposal
    }

    private inline fun findUpdate(solutionEvaluator: SolutionEvaluator, solution: SolutionProposal): DeltaUpdate? {
        for (i in neibourhoodPointer..currentNeighbourhood.lastIndex) {
            val update = currentNeighbourhood[i]
            val lastQueryIteration = update.lastQuery()
            if (lastQueryIteration == 0 || iteration - lastQueryIteration > tabuSize) {
                return update.reevaluateIfNeeded(neibourhoodPointer, solutionEvaluator, solution).also {
                    neibourhoodPointer = i + 1
                }
            }
        }
        return null
    }

        // all are > 0 because we query tabu
    private inline fun forceTabuReevaluate(evaluator: SolutionEvaluator, solution: SolutionProposal): DeltaUpdate {
            val updates = currentUpdates()
            if (breakTabuIfBetterThanTheBest && neibourhoodPointer == 0) {
                val bestUpdate = updates.minBy { it.scoreDelta }!!
                if (isBetter(bestValueSoFar, bestUpdate.scoreDelta + solution.score)) {
                    neibourhoodPointer = Int.MAX_VALUE
                    return bestUpdate
                }
            }
            return updates
                    .minBy { it.lastQuery() }!!
                    .reevaluateIfNeeded(neibourhoodPointer, evaluator, solution).also {
                        // tabu, game over
                        neibourhoodPointer = Int.MAX_VALUE
                    }
    }

    private inline fun currentUpdates() =
            if (neibourhoodPointer == 0) currentNeighbourhood else currentNeighbourhood.drop(neibourhoodPointer)

    private inline fun DeltaUpdate.reevaluateIfNeeded(currentPointer: Int, evaluator: SolutionEvaluator, solution: SolutionProposal) =
            if (currentPointer != 0) {
                val newScore = evaluator.delta(from, to, solution.sequence)
                if (newScore != scoreDelta)
                    copy(scoreDelta = newScore)
                else this
            } else this

    private inline fun DeltaUpdate.lastQuery() = tabuList[from, to]

    override fun canMutate() = true
}

