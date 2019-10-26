package pl.poznan.put.mioib.algorithm.mutators

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal

// Mutates provided solution as long as he can.
// Firstly however, must be asked whether can mutate or not.
interface SolutionMutator {
    // for performance sake evaluation is inside, but normally it should be rather outside
    fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal
    fun canMutate(): Boolean
}
