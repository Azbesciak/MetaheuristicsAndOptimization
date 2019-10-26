package pl.poznan.put.mioib.benchmark

import java.time.Duration


inline fun measureTime(
        minRetries: Int = 0,
        warmUp: Int = 0,
        minDuration: Duration = Duration.ofMillis(1),
        action: () -> Unit
): Double {
    if (warmUp > 0)
        makeWarmUp(warmUp, action)
    val minDurationInNanos = minDuration.toNanos()
    val start = System.nanoTime()
    var endTime: Long
    var counter = 0
    do {
        action()
        ++counter
        endTime = System.nanoTime()
    } while (endTime - start < minDurationInNanos || counter < minRetries)
    return (endTime - start) / counter.toDouble() / 1e6 // from nano to millis
}

inline fun makeWarmUp(repeat: Int, action: () -> Unit) {
    repeat(repeat) { action() }
}
