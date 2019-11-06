package pl.poznan.put.mioib.model

data class DeltaUpdate(
        val from: Int,
        val to: Int,
        val scoreDelta: Double
)
