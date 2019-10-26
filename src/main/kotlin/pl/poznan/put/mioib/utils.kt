package pl.poznan.put.mioib

import kotlin.random.Random

val emptySignsRegex = "\\s+".toRegex()

fun String.splitByWhiteSpace() = trim().split(emptySignsRegex)

val String.looksLikePositiveNumber
    get() = trimStart().firstOrNull()?.isDigit() == true

fun IntArray.swappedAt(first: Int, second: Int) =
        copyOf().also { it.swap(first, second) }

fun IntArray.swap(first: Int, second: Int) {
    val temp = get(first)
    set(first, get(second))
    set(second, temp)
}

fun IntArray.shuffled(random: Random = DEFAULT_RANDOM) =
        copyOf().also { it.shuffle(random) }

fun IntArray.shuffle(random: Random = DEFAULT_RANDOM) {
    (size - 1 downTo 1).forEach { i ->
        val index = random.nextInt(i + 1)
        swap(i, index)
    }
}

private val DEFAULT_RANDOM = Random(1234)
