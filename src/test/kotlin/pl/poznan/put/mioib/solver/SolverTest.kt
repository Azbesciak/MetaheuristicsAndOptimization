package pl.poznan.put.mioib.solver

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.stopcondition.StopCondition
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.model.Weighting
import kotlin.test.assertEquals

internal class SolverTest {

    @Test
    fun returnsOnlyTheBestResult() {
        val instance = Instance("xyz", Weighting.EUC_2D, listOf())
        val maxIterations = 5
        var counter = 0
        val results = doubleArrayOf(50.0, 150.0, 30.0, 60.0, 70.0)
        val mutator = mock<SolutionMutator> {
            on { mutate(any(), any()) } doAnswer {
                SolutionProposal(intArrayOf(counter), results[counter])
            }
            on { canMutate() } doAnswer { ++counter < maxIterations }
        }
        val stopCondition = mock<StopCondition> {}
        val evaluator = mock<SolutionEvaluator> {
            on { solution(any()) } doReturn 100.0 // max value let say
        }
        val solution = Solver.solve(instance, stopCondition, evaluator, mutator) { o, n -> n.score < o.score }
        val bestSolutionIndex = 2
        assertEquals(SolutionProposal(intArrayOf(bestSolutionIndex), results[bestSolutionIndex]), solution)
    }
}
