package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class SteepestNeighbourhoodBrowser(
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        var best = DeltaUpdate(0,0,0.0)
        upperTriangleNeighbourhoodBrowser(indices, evaluator) { from, to, result ->
                if (isBetter(best.scoreDelta, result))
                    best = DeltaUpdate(from, to, result)
        }
        return if (best.from == best.to) emptyList() else listOf(best)
    }
}
