package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.DeltaUpdate

interface NeighbourhoodBrowser {
    fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate>
}
