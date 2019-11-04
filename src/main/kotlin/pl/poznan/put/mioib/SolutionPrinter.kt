package pl.poznan.put.mioib

import java.math.BigDecimal
import java.math.RoundingMode

class SolutionPrinter {
    private val columns = listOf(
            "Instance", "Algorithm",
            "avgTime [ms/inst]", "avgScore",
            "bestScore", "worstScore",
            "instanceBest",
            "bestSimilarity", "avgSimilarity"
    )
    private val separator = " \t| "
    fun init() {
        println(columns.joinToString(separator))
    }

    fun update(instance: String, algorithm: String,
               avgTime: Double, avgScore: Double,
               bestScore: Double, worstScore: Double,
               instanceBest: Double,
               bestQuality: Double, avgQuality: Double
    ) {
        val line = listOf(instance, algorithm) +
                listOf(avgTime, avgScore, bestScore, worstScore, instanceBest, bestQuality, avgQuality)
                        .map { it.rounded().toString() }
        println(line.joinToString(separator))
    }

    private fun Double.rounded() = BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
}
