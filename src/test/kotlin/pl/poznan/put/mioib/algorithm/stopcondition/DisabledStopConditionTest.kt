package pl.poznan.put.mioib.algorithm.stopcondition

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.model.SolutionProposal

internal class DisabledStopConditionTest {

    @Test
    fun shouldStop() {
        val mockSol = SolutionProposal(intArrayOf(), 0.0)
        (0..5).forEach {
            val disabledSC = DisabledStopCondition(it, mock {
                on { shouldStop(any()) } doReturn true
            })
            repeat(it) {
                assertFalse(disabledSC.shouldStop(mockSol))
            }
            assertTrue(disabledSC.shouldStop(mockSol))
        }

    }
}
