package pl.poznan.put.mioib.algorithm.weight

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.makeSymmetricMockWeightMatrix
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Location
import pl.poznan.put.mioib.model.Weighting
import pl.poznan.put.mioib.weight

internal class SymmetricSolutionEvaluatorTest {

    private val instanceIndices = (0 until 4).toList().toIntArray()
    private val solutionEvaluator = SymmetricSolutionEvaluator(
            SymmetricWeightMatrix(
                    Instance("",
                            Weighting.EUC_2D,
                            instanceIndices.map { Location(0, 0.0, it.toDouble()) }
                    ),
                    Euclides2DWeightCalculator
            )
    )

    companion object {
        @JvmStatic
        fun solutions() = listOf(
                solution(6.0, 0, 1, 2, 3),
                solution(8.0, 0, 2, 1, 3),
                solution(6.0, 0, 2, 3, 1),
                solution(8.0, 3, 0, 2, 1)
        )

        private fun solution(result: Double, vararg indices: Int) =
                Arguments.of(result, indices)

        @JvmStatic
        fun deltas() = listOf(
                delta(2.0, 1, 2, 0, 1, 2, 3),
                delta(-2.0, 1, 2, 0, 2, 1, 3),
                delta(0.0, 0, 2, 0, 1, 2, 3),
                delta(0.0, 2, 0, 0, 1, 2, 3),
                delta(0.0, 0, 0, 0, 1, 2, 3),
                delta(0.0, 1, 3, 0, 1, 2, 3)
        )

        private fun delta(result: Double, from: Int, to: Int, vararg indices: Int) =
                Arguments.of(result, from, to, indices)
    }

    @ParameterizedTest
    @MethodSource("solutions")
    fun testSolutionValueCalculation(expected: Double, solution: IntArray) {
        assertEquals(expected, solutionEvaluator.solution(solution), 1e-4)
    }

    @ParameterizedTest
    @MethodSource("deltas")
    fun testDeltaCalculation(expected: Double, from: Int, to: Int, indices: IntArray) {
        assertEquals(expected, solutionEvaluator.delta(from, to, indices), 1e-4)
    }

    @Test
    fun checkFirstAndLast() {
        val matrix = makeSymmetricMockWeightMatrix(
                0 to 1 weight 1,
                0 to 2 weight 3,
                0 to 3 weight 16,
                0 to 4 weight 25,
                1 to 2 weight 2,
                1 to 3 weight 4,
                1 to 4 weight 6,
                2 to 3 weight 7,
                2 to 4 weight 14,
                3 to 4 weight 5
        )

        val result = SymmetricSolutionEvaluator(matrix).delta(0, 4, intArrayOf(0, 1, 2, 3, 4))
        assertEquals(16.0, result)
    }
}
