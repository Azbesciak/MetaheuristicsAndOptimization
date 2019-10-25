package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.model.SolutionProposal

class AnyStopCondition(
        private vararg val conditions: StopCondition
) : StopCondition {
    override fun shouldStop(solution: SolutionProposal) =
            conditions.any { it.shouldStop(solution) }

    override fun initialize() =
            conditions.forEach { it.initialize() }
}
