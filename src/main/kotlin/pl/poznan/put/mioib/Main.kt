package pl.poznan.put.mioib

import pl.poznan.put.mioib.benchmark.measureTime

fun main(args: Array<String>) = ProgramExecutor {
    val values = IntArray(instanceSize) { it }
    val averageTime = measureTime(
            minRetries, warmUp, timeOut
    ) { values.shuffle() }
    println("TIME: $averageTime ns/instance")
}.main(args)

