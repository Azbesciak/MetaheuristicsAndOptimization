package pl.poznan.put.mioib

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import java.time.Duration

class ProgramExecutor(private val task: Params.() -> Unit) : CliktCommand() {
    private val instancePath by option(help = "Instance path without extension; may be shortcut").required()
    private val solutionValuesPath by option(help = "Solutions' values path").required()
    private val minRetries by option(help = "min number of retries").int().default(0).validate { it >= 10 }
    private val warmUp by option(help = "warm up iterations").int().default(0).validate { it >= 0 }
    private val minDuration by option(help = "minimum execution time").default("PT1.0S")
    private val notImprovingSolutions by option(help = "max number of not improving solutions")
            .int().default(0).validate { it >= 0 }
    private val dumpInterval by option(help = "how often inner solutions should be dumped")
            .int().default(Int.MAX_VALUE).validate { it >= 0 }
    private val randomSeed by option(help = "random seed value").int().default(1234)
    private val solutionsToCollect by option(help = "number of solutions to collect").int().default(300)
    private val showProgress by option(help = "should show progress").default("false")
    private val tabuRatio by option(help = "ratio (based on instance size) of tabu list")
            .double().default(0.1).validate { it > 0 }
    private val tabuUpdate by option(help = "size (based on instance size) of ranked updates")
            .double().default(0.1).validate { it > 0 }

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
                showProgress = showProgress.toLowerCase() == "true",
                dumpInterval = dumpInterval,
                tabuRatio = tabuRatio,
                tabuUpdates = tabuUpdate
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
        val showProgress: Boolean,
        val dumpInterval: Int,
        val tabuRatio: Double,
        val tabuUpdates: Double
)
