package pl.poznan.put.mioib.algorithm

import pl.poznan.put.mioib.model.Location

interface EdgeWeightCalculator {
    fun calculate(from: Location, to: Location): Double
}

