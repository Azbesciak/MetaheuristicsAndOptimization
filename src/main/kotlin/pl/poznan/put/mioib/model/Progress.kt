package pl.poznan.put.mioib.model

data class Progress (
        var steps: MutableList<Triple<Int, Double, Double>> = mutableListOf() // <Iteration, Score>
)