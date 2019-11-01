package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

class GreedyNeighbourhoodBrowser(
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        (0 until indices.size - 1).forEach { from ->
            (from + 1 until indices.size).forEach { to ->
                val result = evaluator.delta(from, to, indices)
                if (result < 0){
                    return listOf( DeltaUpdate(from, to, result))
                }
            }
        }
        return emptyList()
    }
}
