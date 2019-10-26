package pl.poznan.put.mioib.solver

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.stopcondition.StopCondition
import pl.poznan.put.mioib.algorithm.weight.SolutionComparator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.SolutionProposal

object Solver {
    fun solve(
            instance: Instance,
            stopCondition: StopCondition,
            evaluator: SolutionEvaluator,
            mutator: SolutionMutator,
            isBetter: SolutionComparator
    ): SolutionProposal {
        val initialSequence = instance.locations.indices.toList().toIntArray()
        var best = SolutionProposal(initialSequence, evaluator.solution(initialSequence))
        stopCondition.initialize()
        var recentSolution = best
        while (!stopCondition.shouldStop(recentSolution) && mutator.canMutate()) {
            val recentSolutionCopy = recentSolution.copy(sequence = recentSolution.sequence.clone())
            recentSolution = mutator.mutate(recentSolutionCopy, evaluator)
            require(recentSolution.score >= 0) {
                "${instance.name}: solution score is negative: $recentSolution"
            }
            if (isBetter(best, recentSolution))
                best = recentSolution
        }
        return best
    }
}
