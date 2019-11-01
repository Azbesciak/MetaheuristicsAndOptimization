package pl.poznan.put.mioib

import com.google.gson.GsonBuilder
import pl.poznan.put.mioib.report.Summary
import java.io.File
import java.lang.Exception
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

fun Summary.save(dir: String="summary"): String? {
    return try {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(this)
        val filename = "${this.name}(${this.type}).sum"
        val directory = File(dir)

        directory.mkdirs()

        File(directory, filename).writeText(jsonString)
        "$directory/$filename"
    }
    catch (e: Exception){
        print("Error: Couldn't save Summary\n$e")
        null
    }
}

private val DEFAULT_RANDOM = Random(1234)
