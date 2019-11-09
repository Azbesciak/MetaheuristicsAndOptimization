package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.DeltaUpdate

class StatefulBrowser(
        var currentFrom: Int,
        private val delegate: NeighbourhoodBrowser
): NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        val res = delegate.browse(indices, evaluator)
        if (res.isNotEmpty())
            currentFrom = res.first().from
        return res
    }

}
