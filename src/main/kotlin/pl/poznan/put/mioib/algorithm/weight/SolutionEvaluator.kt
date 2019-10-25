package pl.poznan.put.mioib.algorithm.weight

interface SolutionEvaluator {
    fun solution(sequence: IntArray): Double
    fun delta(firstIndex: Int, secondIndex: Int, sequence: IntArray): Double
}
