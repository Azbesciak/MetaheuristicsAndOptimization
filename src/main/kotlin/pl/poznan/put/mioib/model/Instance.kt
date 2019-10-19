package pl.poznan.put.mioib.model


data class Instance(
        val name: String,
        val weight: Weighting,
        val locations: List<Location>
) {
    val size get() = locations.size
}

data class Location(val id: Int, val x: Double, val y: Double)

enum class Weighting {
    EUC_2D,
    GEO
}
