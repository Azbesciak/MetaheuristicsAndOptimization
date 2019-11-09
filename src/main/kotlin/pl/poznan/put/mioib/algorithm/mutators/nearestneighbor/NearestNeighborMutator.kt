package pl.poznan.put.mioib.algorithm.mutators.nearestneighbor

import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.algorithm.weight.WeightMatrix
import pl.poznan.put.mioib.model.SolutionProposal

class NearestNeighborMutator(
        private val weightMatrix: WeightMatrix,
        private val isBetter: SolutionValueComparator,
        private val rootLocationIndexSource: (bound: Int) -> Int
) : SolutionMutator {
    private var wasUsed = false
    override fun mutate(solution: SolutionProposal, solutionEvaluator: SolutionEvaluator): SolutionProposal {
        val indicesNumber = solution.sequence.size
        val start = rootLocationIndexSource(indicesNumber)
        var current = start
        val sequence = IntArray(indicesNumber)
        val visited = BooleanArray(indicesNumber)
        sequence[0] = current
        visited[current] = true
        (1 until indicesNumber).forEach {
            current = findNextIndice(visited, current, sequence, it)
        }
        wasUsed = true
        return SolutionProposal(sequence, solutionEvaluator.solution(sequence))
    }

    private fun findNextIndice(visited: BooleanArray, current: Int, sequence: IntArray, it: Int): Int {
        var best = -1
        var bestValue = -1.0
        visited.forEachIndexed visitedLoop@{ i, wasVisited ->
            if (wasVisited || i == current) return@visitedLoop
            val currentConnectionWeight = weightMatrix[current, i]
            if (best == -1 || isBetter(bestValue, currentConnectionWeight)) {
                best = i
                bestValue = currentConnectionWeight
            }
        }
        visited[best] = true
        sequence[it] = best
        return best
    }

    override fun canMutate() = !wasUsed

}
