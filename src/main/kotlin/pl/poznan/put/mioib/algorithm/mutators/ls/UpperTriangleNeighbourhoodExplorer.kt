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
        seed: (Int) -> Int,
        consumer: (from: Int, to: Int, delta: Double) -> Unit
) {
    val start = seed(indices.size - 1)
    (start until indices.size - 1).forEach { from ->
        browse(from, evaluator, indices, consumer)
    }
    (0 until start).forEach { from ->
        browse(from, evaluator, indices, consumer)
    }
}

inline fun browse(
        from: Int, evaluator: SolutionEvaluator,
        indices: IntArray, consumer: (from: Int, to: Int, delta: Double) -> Unit
) {
    (from + 1 until indices.size).forEach { to ->
        val delta = evaluator.delta(from, to, indices)
        consumer(from, to, delta)
    }
}
