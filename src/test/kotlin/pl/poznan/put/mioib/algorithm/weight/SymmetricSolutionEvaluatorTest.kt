package pl.poznan.put.mioib.algorithm.weight

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Location
import pl.poznan.put.mioib.model.Weighting

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
        val matrix = mock<WeightMatrix> {
            fun makePair(from: Int, to: Int, value: Int) {
                on { get(from, to) } doReturn value.toDouble()
                on { get(to, from) } doReturn value.toDouble()
            }
            makePair(0, 1, 1)
            makePair(0, 2, 3)
            makePair(0, 3, 16)
            makePair(0, 4, 25)
            makePair(1, 2, 2)
            makePair(1, 3, 4)
            makePair(1, 4, 6)
            makePair(2, 3, 7)
            makePair(2, 4, 14)
            makePair(3, 4, 5)
        }

        val result = SymmetricSolutionEvaluator(matrix).delta(0, 4, intArrayOf(0, 1, 2, 3, 4))
        assertEquals(16.0, result)
    }
}
