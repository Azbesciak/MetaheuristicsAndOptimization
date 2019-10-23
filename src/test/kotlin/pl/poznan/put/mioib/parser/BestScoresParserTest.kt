package pl.poznan.put.mioib.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class BestScoresParserTest {

    @Test
    fun parse() {
        val content = """
            bayg29 : 1610
            bays29 : 2020
            berlin52 : 7542
            bier127 : 118282
            brazil58 : 25395
            brd14051 : [468942,469935]
            brg180 : 1950
            burma14 : 3323
            ch130 : 6110
            ch150 : 6528
            d198 : 15780
        """.trimIndent().lines()
        val scores = BestScoresParser.parse("qwe", content)
        assertAll({
            assertEquals(1610.0, scores["bayg29"]) { "bayg29" }
        }, {
            assertEquals(3323.0, scores["burma14"]) { "burma14" }
        }, {
            assertThrows(
                    IllegalArgumentException::class.java,
                    { scores["brd14051"] },
                    "should throw on removed because of complex var"
            )
        })
    }
}
