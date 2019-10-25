package pl.poznan.put.mioib.algorithm.weight

import pl.poznan.put.mioib.model.Location

interface EdgeWeightCalculator {
    fun calculate(from: Location, to: Location): Double
}

