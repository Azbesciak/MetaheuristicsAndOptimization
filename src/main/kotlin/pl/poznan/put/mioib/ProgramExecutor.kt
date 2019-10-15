package pl.poznan.put.mioib

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import java.time.Duration

class ProgramExecutor(private val task: Params.() -> Unit) : CliktCommand() {
    private val instanceSize by option(help = "Instance size").int().default(100000).validate { it > 0 }
    private val minRetries by option(help = "min number of retries").int().default(100).validate { it >= 0 }
    private val warmUp by option(help = "warm up iterations").int().default(100).validate { it >= 0 }
    private val timeOut by option(help = "minimum execution time").default("PT1.0S")

    override fun run() {
        Params(instanceSize, minRetries, warmUp, timeOut = Duration.parse(timeOut.toUpperCase())).task()
    }
}

data class Params(
        val instanceSize: Int,
        val minRetries: Int,
        val warmUp: Int,
        val timeOut: Duration
)
