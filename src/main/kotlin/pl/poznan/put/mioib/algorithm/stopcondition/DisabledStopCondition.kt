package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.model.SolutionProposal

class DisabledStopCondition(
        private val disableIterationsCount: Int,
        private val delegate: StopCondition
) : StopCondition {
    private var iterations: Int = 0
    private var disabled = false
    override fun shouldStop(solution: SolutionProposal): Boolean {
        if (!disabled) {
            if (++iterations <= disableIterationsCount)
                return false
            disabled = true
        }
        return delegate.shouldStop(solution)
    }

    override fun initialize() {
        iterations = 0
        disabled = false
    }

}
