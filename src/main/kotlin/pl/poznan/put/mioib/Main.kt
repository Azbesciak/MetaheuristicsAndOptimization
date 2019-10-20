package pl.poznan.put.mioib

import pl.poznan.put.mioib.benchmark.measureTime
import java.util.concurrent.ThreadLocalRandom

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
