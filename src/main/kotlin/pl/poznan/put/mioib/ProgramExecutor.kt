package pl.poznan.put.mioib

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import java.time.Duration

class ProgramExecutor(private val task: Params.() -> Unit) : CliktCommand() {
    private val instancePath by option(help = "Instance path without extension; may be shortcut").required()
    private val solutionValuesPath by option(help = "Solutions' values path").required()
    private val minRetries by option(help = "min number of retries").int().default(0).validate { it >= 0 }
    private val warmUp by option(help = "warm up iterations").int().default(0).validate { it >= 0 }
    private val minDuration by option(help = "minimum execution time").default("PT1.0S")
    private val notImprovingSolutions by option(help = "max number of not improving solutions")
            .int().default(100).validate { it >= 0 }
    private val randomSeed by option(help = "random seed value").int().default(1234)
    private val solutionsToCollect by option(help = "number of solutions to collect").int().default(10)
    private val showProgress by option(help = "should show progress").default("false")

    override fun run() {
        Params(
                instancePath = instancePath,
                solutionValuesPath = solutionValuesPath,
                minRetries = minRetries,
                warmUp = warmUp,
                minDuration = Duration.parse(minDuration.toUpperCase()),
                notImprovingSolutions = notImprovingSolutions,
                randomSeed = randomSeed,
                solutionsToCollect = solutionsToCollect,
                showProgress = showProgress.toLowerCase() == "true"
        ).task()
    }
}

data class Params(
        val instancePath: String,
        val solutionValuesPath: String,
        val minRetries: Int,
        val warmUp: Int,
        val minDuration: Duration,
        val notImprovingSolutions: Int,
        val randomSeed: Int,
        val solutionsToCollect: Int,
        val showProgress: Boolean
)
