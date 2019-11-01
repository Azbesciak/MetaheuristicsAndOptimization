package pl.poznan.put.mioib.model

data class SolutionProposal(
        val sequence: IntArray,
        val score: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolutionProposal

        if (!sequence.contentEquals(other.sequence)) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sequence.contentHashCode()
        result = 31 * result + score.hashCode()
        return result
    }
}
