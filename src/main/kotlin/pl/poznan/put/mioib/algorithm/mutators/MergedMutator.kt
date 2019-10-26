package pl.poznan.put.mioib.algorithm.mutators

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal

class MergedMutator(vararg mutators: SolutionMutator) : SolutionMutator {
    private companion object {
        val noOpPlaceholderMutator = object : SolutionMutator {
            override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator) = solution
            override fun canMutate() = false
        }
    }

    private val iter = mutators.iterator()
    private var currentMutator = noOpPlaceholderMutator
    override fun canMutate(): Boolean {
        if (currentMutator.canMutate()) return true
        while (iter.hasNext()) {
            currentMutator = iter.next()
            if (currentMutator.canMutate()) return true
        }
        return false
    }

    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator) =
            currentMutator.mutate(solution, solutionEvaluator)

}
