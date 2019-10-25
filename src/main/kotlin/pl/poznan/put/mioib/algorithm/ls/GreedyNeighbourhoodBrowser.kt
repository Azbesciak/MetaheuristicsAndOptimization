package pl.poznan.put.mioib.algorithm.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

class GreedyNeighbourhoodBrowser(
        private val evaluator: SolutionEvaluator,
        private val isBetter: (old: Double, newOne: Double) -> Boolean
) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray): List<DeltaUpdate> {
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
