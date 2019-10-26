package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

class GreedyNeighbourhoodBrowser(
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        var best: DeltaUpdate? = null
        (0 until indices.size - 1).forEach { from ->
            (from + 1 until indices.size).forEach { to ->
                val result = evaluator.delta(from, to, indices)
                if (best == null || isBetter(best!!.scoreDelta, result))
                    best = DeltaUpdate(from, to, result)
            }
        }
        return listOf(best!!)
    }
}
