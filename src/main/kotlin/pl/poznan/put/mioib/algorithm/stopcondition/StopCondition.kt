package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.model.SolutionProposal

interface StopCondition {
    fun shouldStop(solution: SolutionProposal): Boolean
    fun initialize() {}
}

