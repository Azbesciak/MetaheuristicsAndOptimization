package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

object UpperTriangleNeighbourhoodExplorer : NeighbourhoodExplorer {
    override fun explore(indices: IntArray, consumer: NeighbourhoodConsumer) {
        (0 until indices.size - 1).forEach f1@{ from ->
            (from + 1 until indices.size).forEach f2@{ to ->
                if (consumer(from, to))
                    return@explore
            }
        }
    }
}

inline fun upperTriangleNeighbourhoodBrowser(
        indices: IntArray, evaluator: SolutionEvaluator,
        consumer: (from: Int, to: Int, delta: Double) -> Unit
) {
    (0 until indices.size - 1).forEach { from ->
        (from + 1 until indices.size).forEach { to ->
            val delta = evaluator.delta(from, to, indices)
            consumer(from, to, delta)
        }
    }
}
