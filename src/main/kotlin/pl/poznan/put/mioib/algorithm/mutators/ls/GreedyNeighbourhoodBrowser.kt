package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class GreedyNeighbourhoodBrowser(
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        (0 until indices.size - 1).forEach f1@{ from ->
            (from + 1 until indices.size).forEach f2@{ to ->
                val result = evaluator.delta(from, to, indices)
                if (isBetter(0.0, result)) {
                    return listOf(DeltaUpdate(from, to, result))
                }
            }
        }
        return emptyList()
    }
}
