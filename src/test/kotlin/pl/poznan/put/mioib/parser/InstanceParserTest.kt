package pl.poznan.put.mioib.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.poznan.put.mioib.ST_70
import pl.poznan.put.mioib.TestInstance
import pl.poznan.put.mioib.ULYSSES_16
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Location

internal class InstanceParserTest {

    companion object {
        @JvmStatic
        fun source() = listOf(
                Arguments.of(ST_70, arrayOf(
                        64 to 96, 80 to 39, 69 to 23, 72 to 42, 48 to 67,
                        58 to 43, 81 to 34, 79 to 17, 30 to 23, 42 to 67,
                        7 to 76, 29 to 51, 78 to 92, 64 to 8, 95 to 57,
                        57 to 91, 40 to 35, 68 to 40, 92 to 34, 62 to 1,
                        28 to 43, 76 to 73, 67 to 88, 93 to 54, 6 to 8,
                        87 to 18, 30 to 9, 77 to 13, 78 to 94, 55 to 3,
                        82 to 88, 73 to 28, 20 to 55, 27 to 43, 95 to 86,
                        67 to 99, 48 to 83, 75 to 81, 8 to 19, 20 to 18,
                        54 to 38, 63 to 36, 44 to 33, 52 to 18, 12 to 13,
                        25 to 5, 58 to 85, 5 to 67, 90 to 9, 41 to 76,
                        25 to 76, 37 to 64, 56 to 63, 10 to 55, 98 to 7,
                        16 to 74, 89 to 60, 48 to 82, 81 to 76, 29 to 60,
                        17 to 22, 5 to 45, 79 to 70, 9 to 100, 17 to 82,
                        74 to 67, 10 to 68, 48 to 19, 83 to 86, 84 to 94
                )),
                Arguments.of(ULYSSES_16, arrayOf(
                        38.24 to 20.42, 39.57 to 26.15, 40.56 to 25.32,
                        36.26 to 23.12, 33.48 to 10.54, 37.56 to 12.19,
                        38.42 to 13.11, 37.52 to 20.44, 41.23 to 9.10,
                        41.17 to 13.05, 36.08 to -5.21, 38.47 to 15.13,
                        38.15 to 15.35, 37.51 to 15.17, 35.49 to 14.32,
                        39.36 to 19.56
                ))
        )
    }

    @ParameterizedTest
    @MethodSource("source")
    fun parse(instance: TestInstance, loc: Array<Pair<Number, Number>>) {
        val expectedInstance = Instance(
                name = instance.id,
                weight = instance.weight,
                locations = locations(loc)
        )
        val result = InstanceParser.parse(instance.id, instance.instance)
        assertEquals(expectedInstance, result) { "instance ${instance.id} is different" }
    }

    private fun locations(locations: Array<out Pair<Number, Number>>) = locations
            .mapIndexed { i, (from, to) -> Location(i + 1, from.toDouble(), to.toDouble()) }
}
