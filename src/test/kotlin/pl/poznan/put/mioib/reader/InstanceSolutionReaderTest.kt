package pl.poznan.put.mioib.reader

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.ST_70

class InstanceSolutionReaderTest {
    @Test
    fun checkCorrectRead() {
        val instanceAndSolution = InstanceSolutionReader.read(ST_70.path)
        assertEquals(1, instanceAndSolution.size) {
            "expected exactly one instance - solution and instance files"
        }
    }

}
