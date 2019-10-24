package pl.poznan.put.mioib.model

data class Summary(
    val name: String,
    val scoreEntries: MutableList<Score>,
    val time: Double
) {
}
