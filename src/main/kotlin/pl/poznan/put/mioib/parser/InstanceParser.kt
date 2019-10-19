package pl.poznan.put.mioib.parser

import pl.poznan.put.mioib.looksLikePositiveNumber
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Location
import pl.poznan.put.mioib.model.Weighting
import pl.poznan.put.mioib.splitByWhiteSpace


object InstanceParser : ContentParser<Instance> {
    override val extension = "tsp"
    override fun parse(contentName: String, content: List<String>): Instance {
        val (locations, meta) = content.partition { it.looksLikePositiveNumber }
        val weight = readWeighting(meta)
        return Instance(
                name = contentName,
                weight = weight,
                locations = locations.map { it.coordinateLocation }
        )
    }

    private fun readWeighting(meta: List<String>): Weighting {
        val weightingType = meta
                .first { it.startsWith("EDGE_WEIGHT_TYPE") }
                .split(":")
                .last()
                .trim()
        require(weightingType == Weighting.EUC_2D.name || weightingType == Weighting.GEO.name) {
            "weightings other than EUC_2D or GEO not supported"
        }
        return Weighting.valueOf(weightingType)
    }

    private val String.coordinateLocation: Location
        get() = splitByWhiteSpace().let { (id, x, y) ->
            Location(
                    id = id.toInt(),
                    x = x.toDouble(),
                    y = y.toDouble()
            )
        }
}

