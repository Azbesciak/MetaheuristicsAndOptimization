package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.model.DeltaUpdate

class GreedyNeighbourhoodBrowser(
        private val initialValue: Double,
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        upperTriangleNeighbourhoodBrowser(indices, evaluator) { from, to, result ->
            if (isBetter(initialValue, result)) {
                return listOf(DeltaUpdate(from, to, result))
            }
        }
        return emptyList()
    }
}
