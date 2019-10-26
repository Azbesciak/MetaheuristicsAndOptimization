package pl.poznan.put.mioib.algorithm.stopcondition

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.model.SolutionProposal


class NotImprovingSolutionStopConditionTest {
    private val condition = NotImprovingSolutionStopCondition(2) { old, newOne ->
        old.score < newOne.score
    }

    @Test
    fun shouldStopAfterLimitReached() {
        condition.run {
            initialize()
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 10.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            assertTrue(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
        }
    }

    @Test
    fun shouldBeResetedOnInitialize() {
        condition.run {
            initialize()
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 10.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            initialize()
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
        }
    }

    @Test
    fun shouldBeResetedOnBetterSolution() {
        condition.run {
            initialize()
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 10.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 9.0)))
            assertFalse(shouldStop(SolutionProposal(intArrayOf(), 11.0)))
        }
    }
}
