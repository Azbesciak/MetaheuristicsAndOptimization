package pl.poznan.put.mioib.report

data class Attempt (
    val score: Double,
    val steps: MutableList<Triple<Int, Double, Double>>

)