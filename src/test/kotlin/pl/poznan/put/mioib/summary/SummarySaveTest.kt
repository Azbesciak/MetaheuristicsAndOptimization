package pl.poznan.put.mioib.summary

import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.report.Summary
import kotlin.test.assertNotNull

internal class SummarySaveTest {

    @Test
    fun save() {
        val summary = Summary("test", 0.0)
        val tempDir = createTempDir()
        val filename = summary.save(tempDir.path)
        assertNotNull(filename) { "Summary file not exist" }
    }
}
