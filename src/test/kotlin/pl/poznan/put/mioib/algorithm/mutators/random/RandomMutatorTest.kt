package pl.poznan.put.mioib.algorithm.mutators.random

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.SolutionProposal
import kotlin.random.Random

internal class RandomMutatorTest {

    @Test
    fun isPredictable() {
        val rand1 = Random(12)
        val mut1 = RandomMutator(rand1, 2)
        val firstSolution = SolutionProposal(intArrayOf(1, 2, 3), 10.0)
        val eval = mock<SolutionEvaluator> {
            on { solution(any()) } doReturn (20.0)
        }
        val first = mut1.mutate(firstSolution, eval)
        val second = mut1.mutate(firstSolution, eval)
        assertNotEquals(first, second) { "solutions should differ" }
        val rand2 = Random(12)
        val mut2 = RandomMutator(rand2, 2)
        assertEquals(first, mut2.mutate(firstSolution, eval))
        assertEquals(second, mut2.mutate(firstSolution, eval))
    }
}
