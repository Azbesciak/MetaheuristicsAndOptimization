package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal

class LocalSearchMutator(
        private val browser: NeighbourhoodBrowser
) : SolutionMutator {
    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        val (update) = browser.browse(solution.sequence, solutionEvaluator).ifEmpty { return solution }
        return solution updatedWith update
    }

    override fun canMutate() = true
}
