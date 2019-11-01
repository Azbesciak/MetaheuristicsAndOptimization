package pl.poznan.put.mioib.model

data class Progress (
        var steps: MutableList<Pair<Int, Double>> = mutableListOf() // <Iteration, Score>
)