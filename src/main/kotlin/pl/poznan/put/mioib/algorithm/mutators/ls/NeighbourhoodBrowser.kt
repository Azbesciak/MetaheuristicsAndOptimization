package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator

interface NeighbourhoodBrowser {
    fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate>
}

data class DeltaUpdate(
        val from: Int,
        val to: Int,
        val scoreDelta: Double
)
