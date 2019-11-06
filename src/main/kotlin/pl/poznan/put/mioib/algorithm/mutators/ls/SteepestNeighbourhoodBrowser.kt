package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class SteepestNeighbourhoodBrowser(
        private val initialValue: Double,
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        var best = initialValue
        var bestFrom = -1
        var bestTo = -1
        upperTriangleNeighbourhoodBrowser(indices, evaluator) { from, to, result ->
            if (isBetter(best, result)) {
                best = result
                bestFrom = from
                bestTo = to
            }
        }
        return if (bestFrom == -1) emptyList() else listOf(DeltaUpdate(bestFrom, bestTo, best))
    }
}
