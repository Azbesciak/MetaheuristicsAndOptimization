package pl.poznan.put.mioib.benchmark.similarity

object SameSequenceOccurrenceSimilarity : SimilarityMeasure {
    override fun measure(seq1: IntArray, seq2: IntArray): Double {
        if (seq1.size <= 2) {
            return if (seq1.sorted() == seq2.sorted()) 1.0
            else 0.0
        }
        val firstSeqSet = seq1.toSequencesSet()
        val secondSeqSet = seq2.toSequencesSet()
        val intersection = firstSeqSet intersect secondSeqSet
        return intersection.size / firstSeqSet.size.toDouble()
    }

    private fun IntArray.toSequencesSet() =
            (toList().windowed(2) + listOf(listOf(first(), last())))
                    .map {
                        it.sorted().let { (f, s) -> f to s }
                    }.toSet()
}
