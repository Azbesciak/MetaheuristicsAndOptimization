package pl.poznan.put.mioib.algorithm.mutators.ls

interface NeighbourhoodExplorer {
    fun explore(indices: IntArray, consumer: NeighbourhoodConsumer)
}

// returns true if exploration is finished
typealias NeighbourhoodConsumer = (from: Int, to: Int) -> Boolean
