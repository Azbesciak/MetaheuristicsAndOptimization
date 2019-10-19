package pl.poznan.put.mioib.model

data class Solution(
        val instanceName: String,
        val sequence: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Solution

        if (instanceName != other.instanceName) return false
        if (!sequence.contentEquals(other.sequence)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instanceName.hashCode()
        result = 31 * result + sequence.contentHashCode()
        return result
    }
}
