package pl.poznan.put.mioib.benchmark.similarity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.*
import org.junit.jupiter.params.provider.MethodSource

internal class SameSequenceOccurrenceSimilarityTest {

    companion object {
        @JvmStatic
        fun sameSequence() = listOf(
                seq(1),
                seq(1, 2),
                seq(1, 2, 3),
                seq(1, 2, 3, 4),
                seq(5, 4, 3, 1, 2),
                seq(1, 0, 9, 4, 3, 6, 2),
                seq(1, 4, 6, 8, 10),
                seq(1, 2, 3, 4, 5, 6),
                seq(1, 5, 3, 7, 4, 8, 2, 0)
        )

        private fun seq(vararg value: Int) = of(value)

        @JvmStatic
        fun changedSequence() = listOf(
                of(intArrayOf(1,2,3,4), intArrayOf(3,4,1,2), 0),
                of(intArrayOf(1,2,3,4,5,6), intArrayOf(4,5,6,1,2,3), 0),
                of(intArrayOf(1,2,3,4,5,6), intArrayOf(1,2,4,3,5,6), 2),
                of(intArrayOf(1,2,3,4,5,6,7,8,9), intArrayOf(1,3,2,5,4,6,7,8,9), 3)
        )

    }

    @ParameterizedTest
    @MethodSource("sameSequence")
    fun `should return 1 for the same sequence`(seq: IntArray) {
        assertEquals(1.0, SameSequenceOccurrenceSimilarity.measure(seq, seq)) {
            "same sequence should have full similarity"
        }
    }

    @ParameterizedTest
    @MethodSource("sameSequence")
    fun `should return 1 for reversed sequence`(seq: IntArray) {
        val another = seq.reversed().toIntArray()
        assertEquals(1.0, SameSequenceOccurrenceSimilarity.measure(seq, another)) {
            "reversed sequence should have zero similarity"
        }
    }

    @ParameterizedTest
    @MethodSource("changedSequence")
    fun `should include only assumed changes`(source: IntArray, another: IntArray, changes: Int) {
        val result = SameSequenceOccurrenceSimilarity.measure(source, another)
        val expected = 1 - (changes / another.size.toDouble())
        assertEquals(expected, result, 1e-6) { "different similarity value" }
    }
}
