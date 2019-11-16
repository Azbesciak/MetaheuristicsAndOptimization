package pl.poznan.put.mioib.algorithm.mutators.ts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.mockEvaluator
import pl.poznan.put.mioib.model.DeltaUpdate
import pl.poznan.put.mioib.weight

internal class RankerNeighbourhoodBrowserTest {

    @Test
    fun browse() {
        val resultMaxSize = 3
        val browser = RankerNeighbourhoodBrowser(resultMaxSize, { a, b -> b > a }, { 0 })
        val items = arrayOf(
                0 to 1 weight 4,
                0 to 2 weight 3,
                0 to 3 weight 2,
                0 to 4 weight -10,
                1 to 2 weight 10,
                1 to 3 weight 6,
                1 to 4 weight -5,
                2 to 3 weight 2,
                2 to 4 weight 7
        )
        val evaluator = mockEvaluator(*items)
        val result = browser.browse(intArrayOf(0, 1, 2, 3, 4), evaluator)
        assertEquals(2, result.size) { "invalid result size" }
        assertEquals(listOf(DeltaUpdate(from=1, to=2, scoreDelta=10.0), DeltaUpdate(from=3, to=4, scoreDelta=0.0)), result) { "expected sorted result by weight descending" }

    }
}

