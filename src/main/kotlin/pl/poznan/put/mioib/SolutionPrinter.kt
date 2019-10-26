package pl.poznan.put.mioib

import java.math.BigDecimal
import java.math.RoundingMode

class SolutionPrinter {
    private val columns = listOf("Instance", "avgTime [ms/inst]", "avgScore", "bestScore", "worstScore", "instanceBest")
    private val separator = " \t| "
    fun init() {
        println(columns.joinToString(separator))
    }

    fun update(instance: String, avgTime: Double, avgScore: Double, bestScore: Double, worstScore: Double, instanceBest: Double) {
        val line = listOf(instance) + listOf(avgTime, avgScore, bestScore, worstScore, instanceBest).map { it.rounded().toString() }
        println(line.joinToString(separator))
    }

    private fun Double.rounded() = BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
}
