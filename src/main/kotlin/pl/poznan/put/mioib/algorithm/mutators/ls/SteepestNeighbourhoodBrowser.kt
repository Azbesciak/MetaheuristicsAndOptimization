package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class SteepestNeighbourhoodBrowser(
        private val explorer: NeighbourhoodExplorer,
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        var best: DeltaUpdate? = null
        explorer.explore(indices) { from, to ->
            val result = evaluator.delta(from, to, indices)
            if (best == null || isBetter(best!!.scoreDelta, result))
                best = DeltaUpdate(from, to, result)
            false
        }

        return listOf(best!!)
    }
}
