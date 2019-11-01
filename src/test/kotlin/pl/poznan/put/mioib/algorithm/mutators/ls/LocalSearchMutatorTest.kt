package pl.poznan.put.mioib.algorithm.mutators.ls

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal
import kotlin.test.assertEquals

internal class LocalSearchMutatorTest {

    @Test
    fun shouldUseBestBrowserSolution() {
        val firstQuery = intArrayOf(1, 2, 3, 4)
        val secondQuery = intArrayOf(1, 2, 4, 3)
        val evaluator = mock<SolutionEvaluator> {}
        val browser = mock<NeighbourhoodBrowser> {
            on { browse(firstQuery, evaluator) } doReturn (listOf(DeltaUpdate(2, 3, 10.0)))
            on { browse(secondQuery, evaluator) } doReturn (listOf(DeltaUpdate(1, 2, 15.0)))
        }
        val mutator = LocalSearchMutator(browser)
        assertEquals(
                SolutionProposal(intArrayOf(1, 2, 4, 3), 30.0),
                mutator.mutate(SolutionProposal(firstQuery, 20.0), evaluator)
        )
        assertEquals(
                SolutionProposal(intArrayOf(1, 4, 2, 3), 25.0),
                mutator.mutate(SolutionProposal(secondQuery, 10.0), evaluator)
        )
    }
}
