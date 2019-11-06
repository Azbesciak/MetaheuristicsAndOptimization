package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class GreedyNeighbourhoodBrowser(
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        upperTriangleNeighbourhoodBrowser(indices, evaluator) { from, to, result ->
            if (isBetter(0.0, result)) {
                return listOf(DeltaUpdate(from, to, result))
            }
        }
        return emptyList()
    }
}
