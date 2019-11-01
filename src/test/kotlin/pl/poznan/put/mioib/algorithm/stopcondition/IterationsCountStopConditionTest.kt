package pl.poznan.put.mioib.algorithm.stopcondition

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.model.SolutionProposal

internal class IterationsCountStopConditionTest {

    @Test
    fun shouldStop() {
        val mockSolution = SolutionProposal(intArrayOf(), 0.0)
        (0..5).forEach {
            val st = IterationsCountStopCondition(it)
            repeat(it) { assertFalse(st.shouldStop(mockSolution)) }
            assertTrue(st.shouldStop(mockSolution))
        }
    }
}
