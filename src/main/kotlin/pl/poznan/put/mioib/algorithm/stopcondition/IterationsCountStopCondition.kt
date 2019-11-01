package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.model.SolutionProposal

class IterationsCountStopCondition(private val maxIterations: Int) : StopCondition {
    private var iterationsMade = 0
    override fun shouldStop(solution: SolutionProposal) = ++iterationsMade > maxIterations
    override fun initialize() {
        iterationsMade = 0
    }
}
