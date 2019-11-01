package pl.poznan.put.mioib.algorithm.mutators.ls

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

internal class SteepestNeighbourhoodBrowserTest {

    @Test
    fun browse() {
        val indices = intArrayOf(1, 2, 3, 4, 5)
        val se = mock<SolutionEvaluator> {
            on { delta(0, 1, indices) } doReturn 1.0
            on { delta(0, 2, indices) } doReturn 2.0
            on { delta(1, 3, indices) } doReturn 4.0
            on { delta(4, 1, indices) } doReturn 0.0
        }
        val browser = SteepestNeighbourhoodBrowser(UpperTriangleNeighbourhoodExplorer) { a, b -> a < b }
        val result = browser.browse(indices, se)
        assertEquals(1, result.size) { "Expected only one result" }
        val (from, to, resultValue) = result.first()
        assertAll({
            assertEquals(4.0, resultValue, 1e-2) { "expected the best result" }
        }, {
            assertEquals(1, from) { "different from" }
        }, {
            assertEquals(3, to) { "different to" }
        })
    }
}
