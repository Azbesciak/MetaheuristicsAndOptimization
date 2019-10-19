package pl.poznan.put.mioib.parser

import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.ST_70
import java.io.File
import kotlin.test.assertEquals

internal class FileContentParserTest {

    @Test
    fun checkCorrectRead() {
        val parser = object : ContentParser<List<String>> {
            override val extension = "tsp"
            override fun parse(contentName: String, content: List<String>) = content
        }

        val result = FileContentParser(
                "a",
                listOf(File("${ST_70.path}.${parser.extension}")),
                parser
        )
        assertEquals(ST_70.instance, result)
    }
}
