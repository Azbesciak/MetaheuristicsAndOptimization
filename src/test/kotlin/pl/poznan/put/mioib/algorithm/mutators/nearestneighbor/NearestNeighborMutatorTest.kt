package pl.poznan.put.mioib.algorithm.mutators.nearestneighbor

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.algorithm.weight.MIN_OR_EQUAL_SOLUTION_VALUE
import pl.poznan.put.mioib.algorithm.weight.SymmetricSolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.WeightMatrix
import pl.poznan.put.mioib.makeSymmetricMockWeightMatrix
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.weight

internal class NearestNeighborMutatorTest {

    companion object {
        private val simple4Mat = makeSymmetricMockWeightMatrix(
                0 to 1 weight 10,
                0 to 2 weight 8,
                0 to 3 weight 6,
                1 to 2 weight 4,
                1 to 3 weight 6,
                2 to 3 weight 5
        )

        @JvmStatic
        fun instances() = listOf(
                Arguments.of(simple4Mat, 0, SolutionProposal(intArrayOf(0, 3, 2, 1), 25.0)),
                Arguments.of(simple4Mat, 1, SolutionProposal(intArrayOf(1, 2, 3, 0), 25.0)),
                Arguments.of(simple4Mat, 2, SolutionProposal(intArrayOf(2, 1, 3, 0), 24.0)),
                Arguments.of(simple4Mat, 3, SolutionProposal(intArrayOf(3, 2, 1, 0), 25.0))
        )
    }

    @ParameterizedTest
    @MethodSource("instances")
    fun `should connect the nearest not visited cities`(mat: WeightMatrix, start: Int, expected: SolutionProposal) {
        val mutator = NearestNeighborMutator(mat, MIN_OR_EQUAL_SOLUTION_VALUE) { start }
        val evaluator = SymmetricSolutionEvaluator(mat)
        val sequence = (expected.sequence.indices).toList().toIntArray()
        val proposalSolution = evaluator.solution(sequence)
        val proposal = SolutionProposal(sequence, proposalSolution)
        val result = mutator.mutate(proposal, evaluator)
        Assertions.assertEquals(expected, result) { "wrong solution" }
    }
}
