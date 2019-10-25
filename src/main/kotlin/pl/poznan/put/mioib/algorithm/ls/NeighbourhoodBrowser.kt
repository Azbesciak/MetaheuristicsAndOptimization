package pl.poznan.put.mioib.algorithm.ls

interface NeighbourhoodBrowser {
    fun browse(indices: IntArray): List<DeltaUpdate>
}

data class DeltaUpdate(
        val from: Int,
        val to: Int,
        val scoreDelta: Double
)
