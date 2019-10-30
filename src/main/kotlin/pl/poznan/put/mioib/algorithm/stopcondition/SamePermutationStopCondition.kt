package pl.poznan.put.mioib.algorithm.stopcondition

import pl.poznan.put.mioib.algorithm.weight.SolutionComparator
import pl.poznan.put.mioib.model.SolutionProposal

class SamePermutationStopCondition() : StopCondition {
    private var last: SolutionProposal? = null

    override fun shouldStop(solution: SolutionProposal): Boolean {
        // !this.last!!.sequence.contentEquals(solution.sequence) can freeze if init solution is better than any generated later
        // thats why scores are compared instead
        return if (this.last == null || this.last!!.score != solution.score){
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
