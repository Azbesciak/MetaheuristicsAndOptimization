package pl.poznan.put.mioib.algorithm.weight

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.algorithm.weight.Euclides2DWeightCalculator
import pl.poznan.put.mioib.algorithm.weight.SymmetricWeightMatrix
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Location
import pl.poznan.put.mioib.model.Weighting
import kotlin.math.sqrt

class SymmetricWeightMatrixTest {
    private val instance = Instance(
            name = "test",
            locations = listOf(
                    Location(1, 1.0, 1.0),
                    Location(2, 2.0, 2.0),
                    Location(3, 3.0, 3.0),
                    Location(4, 1.0, 4.0),
                    Location(5, 3.0, 5.0),
                    Location(6, -1.0, 4.0)
            ),
            weight = Weighting.EUC_2D
    )
    private val matrix = SymmetricWeightMatrix(
            instance = instance,
            calculator = Euclides2DWeightCalculator
    )

    companion object {
        const val EPS = 1e-6
        @JvmStatic
        fun source() = listOf(
                args(0, 0, 0.0),
                args(1, 1, 0.0),
                args(0, 1, sqrt(2.0)),
                args(0, 2, sqrt(8.0)),
                args(0, 3, 3.0),
                args(0, 4, sqrt(20.0)),
                args(1, 5, sqrt(13.0))
        )

        private fun args(loc1: Int, loc2: Int, expectedResult: Double) = Arguments.of(loc1, loc2, expectedResult)
    }

    @ParameterizedTest
    @MethodSource("source")
    fun get(loc1: Int, loc2: Int, expectedResult: Double) {
        assertAll("loc1: $loc1, loc2: $loc2", {
            assertEquals(expectedResult, matrix[loc1, loc2], EPS)
        }, {
            assertEquals(expectedResult, matrix[loc2, loc1], EPS)
        })
    }
}
