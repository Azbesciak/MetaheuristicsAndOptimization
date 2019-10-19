package pl.poznan.put.mioib.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.ST_70
import pl.poznan.put.mioib.TestInstance
import pl.poznan.put.mioib.ULYSSES_16
import pl.poznan.put.mioib.model.Solution

internal class SolutionParserTest {

    companion object {
        @JvmStatic
        fun source() = listOf(
                Arguments.of(ST_70, intArrayOf(
                        1, 36, 29, 13, 70, 35, 31, 69, 38, 59, 22,
                        66, 63, 57, 15, 24, 19, 7, 2, 4, 18, 42, 32,
                        3, 8, 26, 55, 49, 28, 14, 20, 30, 44, 68, 27,
                        46, 25, 45, 39, 61, 40, 9, 17, 43, 41, 6, 53,
                        5, 10, 52, 60, 12, 34, 21, 33, 62, 54, 48, 67,
                        11, 64, 65, 56, 51, 50, 58, 37, 47, 16, 23
                )),
                Arguments.of(ULYSSES_16, intArrayOf(
                        1, 14, 13, 12, 7, 6, 15, 5,
                        11, 9, 10, 16, 3, 2, 4, 8
                ))
        )
    }

    @ParameterizedTest
    @MethodSource("source")
    fun parse(instance: TestInstance, expectedSequence: IntArray) {
        val result = SolutionParser.parse(instance.id, instance.solution)
        val expectedSolution = Solution(
                instanceName = instance.id,
                sequence = expectedSequence
        )
        assertEquals(expectedSolution, result) { "solution is different" }
    }
}
