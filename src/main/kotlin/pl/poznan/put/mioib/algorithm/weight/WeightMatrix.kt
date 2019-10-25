package pl.poznan.put.mioib.algorithm.weight

interface WeightMatrix {
    operator fun get(from: Int, to: Int): Double
}
