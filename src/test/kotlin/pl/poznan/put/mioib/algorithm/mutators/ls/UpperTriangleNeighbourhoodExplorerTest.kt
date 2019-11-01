package pl.poznan.put.mioib.algorithm.mutators.ls

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpperTriangleNeighbourhoodExplorerTest {

    @Test
    fun shouldExploreWholeSpaceInGivenOrder() {
        val seq = intArrayOf(0, 1, 2)
        val explorationOrder = mutableListOf<Pair<Int, Int>>()
        UpperTriangleNeighbourhoodExplorer.explore(seq) { from, to ->
            explorationOrder += Pair(from, to)
            false
        }
        assertEquals(listOf(0 to 1, 0 to 2, 1 to 2), explorationOrder) { "wrong exploration order" }
    }

    @Test
    fun shouldReturnWhenSatisfied() {
        val seq = intArrayOf(0, 1, 2, 3, 4, 5)
        var callCounter = 0
        UpperTriangleNeighbourhoodExplorer.explore(seq) { _, _ ->
            callCounter++
            true
        }
        assertEquals(1, callCounter) { "should be called exactly once" }
    }
}
