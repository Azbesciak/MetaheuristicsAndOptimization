package pl.poznan.put.mioib.benchmark

import java.time.Duration
import kotlin.math.min


inline fun measureTime(
        minRetries: Int = 0,
        warmUp: Int = 0,
        minDuration: Duration = Duration.ofMillis(1),
        showProgress: Boolean = false,
        action: () -> Unit
): Double {
    if (warmUp > 0)
        makeWarmUp(warmUp, action)
    val bar = ProgressBar()
    val minDurationInNanos = minDuration.toNanos()
    val start = System.nanoTime()
    var endTime: Long
    var counter = 0
    var elapsedTime: Long
    do {
        action()
        ++counter
        endTime = System.nanoTime()
        elapsedTime = endTime - start
        if (showProgress) {
            val progress = min(elapsedTime / minDurationInNanos.toDouble(), counter / minRetries.toDouble())
            bar.update(progress, counter, minRetries, elapsedTime, minDurationInNanos)
        }
    } while (elapsedTime < minDurationInNanos || counter < minRetries)
    return elapsedTime / counter.toDouble() / 1e6 // from nano to millis
}

inline fun makeWarmUp(repeat: Int, action: () -> Unit) {
    repeat(repeat) { action() }
}
