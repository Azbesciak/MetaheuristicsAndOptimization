package pl.poznan.put.mioib.algorithm

interface WeightMatrix {
    operator fun get(from: Int, to: Int): Double
}
