package pl.poznan.put.mioib.benchmark.similarity

interface SimilarityMeasure {
    fun measure(seq1: IntArray, seq2: IntArray): Double
}
