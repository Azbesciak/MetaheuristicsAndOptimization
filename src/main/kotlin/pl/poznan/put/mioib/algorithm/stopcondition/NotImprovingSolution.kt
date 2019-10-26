package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.algorithm.weight.SolutionComparator
import pl.poznan.put.mioib.model.SolutionProposal

data class NotImprovingSolution(
        private val notImprovingIterations: Int,
        private val isBetter: SolutionComparator
) : StopCondition {
    private var iterationsFromLastImprovement: Int = 0
    private var last: SolutionProposal? = null

    override fun shouldStop(solution: SolutionProposal): Boolean {
        if (iterationsFromLastImprovement > notImprovingIterations) return true
        if (last == null || isBetter(last!!, solution)) {
            last = solution
            iterationsFromLastImprovement = 0
            return false
        }
        return ++iterationsFromLastImprovement > notImprovingIterations
    }

    override fun initialize() {
        iterationsFromLastImprovement = 0
        last = null
    }

}
