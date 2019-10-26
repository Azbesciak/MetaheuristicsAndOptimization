package pl.poznan.put.mioib.parser

import pl.poznan.put.mioib.reader.Reader
import java.io.File

object BestScoresReader : Reader<BestScoresSource> {
    override fun read(path: String) =
            FileContentParser("solutions", listOf(File(path)), BestScoresParser)
}

object BestScoresParser : ContentParser<BestScoresSource> {
    override val extension = "txt"

    override fun parse(contentName: String, content: List<String>) = BestScoresSource(
            content.filterNot { it.trim().endsWith("]") }
                    .map {
                        val (name, value) = it.split(":")
                        name.trim() to value.trim().toDouble()
                    }.toMap()
    )
}

data class BestScoresSource(
        private val scores: Map<String, Double>
) {
    operator fun get(instance: String) = requireNotNull(scores[instance]) {
        "solution for '$instance' not found in scores: $scores"
    }
}


