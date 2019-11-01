package pl.poznan.put.mioib.algorithm.mutators.ls

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
