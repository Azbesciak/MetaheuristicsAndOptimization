package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator

class GreedyNeighbourhoodBrowser(
        private val explorer: NeighbourhoodExplorer,
        private val isBetter: SolutionValueComparator
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        val browseResult = mutableListOf<DeltaUpdate>()
        explorer.explore(indices) { from, to ->
            val result = evaluator.delta(from, to, indices)
            val isSatisfied = isBetter(0.0, result)
            if (isSatisfied) {
                browseResult += DeltaUpdate(from, to, result)
            }
            isSatisfied
        }
        return browseResult
    }
}
