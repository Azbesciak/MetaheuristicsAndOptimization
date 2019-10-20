package pl.poznan.put.mioib.benchmark

import java.time.Duration
import kotlin.system.measureNanoTime


inline fun measureTime(
        minRetries: Int = 0,
        warmUp: Int = 0,
        epsilon: Duration = Duration.ofMillis(1),
        action: () -> Unit
): Double {
    if (warmUp > 0)
        makeWarmUp(warmUp, action)
    val epsilonInNanos = epsilon.nano * 100
    val start = System.nanoTime()
    var endTime: Long
    var counter = 0
    do {
        action()
        ++counter
        endTime = System.nanoTime()
    } while (endTime - start < epsilonInNanos || counter < minRetries)
    return (endTime - start) / counter.toDouble()
}

inline fun makeWarmUp(repeat: Int, action: () -> Unit) {
    println("warm up started")
    repeat(repeat) {
        val time = measureNanoTime(action)
        println("warm up $it: $time ns")
    }
    println("warm up end")
}
