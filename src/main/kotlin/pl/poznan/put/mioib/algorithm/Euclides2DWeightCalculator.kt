package pl.poznan.put.mioib.algorithm

import pl.poznan.put.mioib.model.Location
import kotlin.math.pow
import kotlin.math.sqrt

object Euclides2DWeightCalculator : EdgeWeightCalculator {
    override fun calculate(from: Location, to: Location) =
            sqrt((from.x - to.x).pow(2) + (from.y - to.y).pow(2))
}


