package pl.poznan.put.mioib

val emptySignsRegex = "\\s+".toRegex()

fun String.splitByWhiteSpace() = trim().split(emptySignsRegex)

val String.looksLikePositiveNumber get() = trimStart().firstOrNull()?.isDigit() == true
