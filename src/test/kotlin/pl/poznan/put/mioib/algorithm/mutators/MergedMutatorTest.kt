package pl.poznan.put.mioib.algorithm.mutators

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal
import kotlin.test.assertFalse

internal class MergedMutatorTest {

    @Test
    fun shouldAskEveryMutatorUntilWillDenyToMutate() {
        val onceMutator = countedMutator(1)
        val twiceMutator = countedMutator(2)
        val merged = MergedMutator(onceMutator, twiceMutator)
        var allCalls = 0
        while (merged.canMutate()) {
            allCalls++
        }
        assertEquals(3, allCalls) { "invalid mutators calls number" }
    }

    private fun countedMutator(maxCalls: Int) = mock<SolutionMutator> {
        var calls = 0
        on { canMutate() } doAnswer { ++calls <= maxCalls }
    }

    @Test
    fun shouldUseMutatorsInGivenOrder() {
        val mutator = MergedMutator(
                reorderMutator(1, 2),
                reorderMutator(2, 3),
                reorderMutator(4, 5)
        )
        val solEval = mock<SolutionEvaluator> {}
        val mockSolution = SolutionProposal(intArrayOf(), 0.0)
        assertTrue(mutator.canMutate())
        assertEquals(SolutionProposal(intArrayOf(1,2), 0.0), mutator.mutate(mockSolution, solEval))
        assertTrue(mutator.canMutate())
        assertEquals(SolutionProposal(intArrayOf(2,3), 0.0), mutator.mutate(mockSolution, solEval))
        assertTrue(mutator.canMutate())
        assertEquals(SolutionProposal(intArrayOf(4,5), 0.0), mutator.mutate(mockSolution, solEval))
        assertFalse(mutator.canMutate())
    }

    private fun reorderMutator(from: Int, to: Int) = mock<SolutionMutator> {
        var cals = 0
        on { mutate(any(), any()) } doReturn SolutionProposal(intArrayOf(from, to), 0.0)
        on { canMutate() } doAnswer { ++cals == 1 }
    }
}
