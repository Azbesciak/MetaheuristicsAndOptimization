package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.swappedAt

class LocalSearchMutator(
        private val browser: NeighbourhoodBrowser
) : SolutionMutator {
    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        val (update) = browser.browse(solution.sequence, solutionEvaluator).ifEmpty { return solution }
        return SolutionProposal(
                sequence = solution.sequence.swappedAt(update.from, update.to),
                score = solution.score + update.scoreDelta
        )
    }

    override fun canMutate() = true
}
