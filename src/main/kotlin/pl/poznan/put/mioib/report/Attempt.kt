package pl.poznan.put.mioib.report

data class Attempt (
    val score: Double,
    val steps: MutableList<Pair<Int, Double>>

)