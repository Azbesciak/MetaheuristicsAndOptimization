package pl.poznan.put.mioib.algorithm.mutators.random

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.shuffled
import kotlin.random.Random

class RandomMutator(
        private val random: Random,
        private val maxSolutions: Int
) : SolutionMutator {
    private var solutionsCount: Int = 0

    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        val copy = solution.sequence.shuffled(random)
        val solutionValue = solutionEvaluator.solution(copy)
        solutionsCount++
        return SolutionProposal(
                sequence = copy,
                score = solutionValue
        )
    }

    override fun canMutate() = solutionsCount < maxSolutions
}
