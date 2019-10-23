package pl.poznan.put.mioib.parser

import pl.poznan.put.mioib.looksLikePositiveNumber
import pl.poznan.put.mioib.model.Solution
import pl.poznan.put.mioib.splitByWhiteSpace

class SolutionParser(
        private val scoreProvider: (instance: String) -> Double
) : ContentParser<Solution> {
    override val extension = "opt.tour"
    override fun parse(contentName: String, content: List<String>): Solution {
        return Solution(
                instanceName = contentName,
                sequence = content
                        .dropWhile { !it.looksLikePositiveNumber }
                        .dropLastWhile { !it.looksLikePositiveNumber }
                        .flatMap { it.splitByWhiteSpace() }
                        .map { it.toInt() }
                        .toIntArray(),
                score = scoreProvider(contentName)
        )
    }
}
