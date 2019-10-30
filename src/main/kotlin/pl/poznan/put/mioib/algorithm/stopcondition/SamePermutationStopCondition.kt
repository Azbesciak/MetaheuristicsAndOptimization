package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.algorithm.weight.SolutionComparator
import pl.poznan.put.mioib.model.SolutionProposal

class SamePermutationStopCondition() : StopCondition {
    private var last: SolutionProposal? = null

    override fun shouldStop(solution: SolutionProposal): Boolean {
        return if (this.last == null || !this.last!!.sequence.contentEquals(solution.sequence)){
            this.last = solution
            false
        }
        else{
            true
        }
    }

    override fun initialize() {
        last = null
    }

}
