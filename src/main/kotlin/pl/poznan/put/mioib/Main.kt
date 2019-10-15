package pl.poznan.put.mioib

import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import kotlin.system.measureNanoTime

fun main(args: Array<String>) = ProgramExecutor {
    val values = IntArray(instanceSize) { it }
    val averageTime = measureTime(
            minRetries, warmUp, timeOut
    ) { values.shuffleArray() }
    println("TIME: $averageTime ns/instance")
}.main(args)

fun IntArray.shuffleArray() {
    val rnd = ThreadLocalRandom.current()
    (size - 1 downTo 1).forEach { i ->
        val index = rnd.nextInt(i + 1)
        val a = get(index)
        set(index, get(i))
        set(i, a)
    }
}

private inline fun measureTime(
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

private inline fun makeWarmUp(repeat: Int, action: () -> Unit) {
    println("warm up started")
    repeat(repeat) {
        val time = measureNanoTime(action)
        println("warm up $it: $time ns")
    }
    println("warm up end")
}
