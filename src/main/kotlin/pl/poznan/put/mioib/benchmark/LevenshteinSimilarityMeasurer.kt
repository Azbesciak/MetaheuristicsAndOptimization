package pl.poznan.put.mioib.benchmark

// https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Kotlin
object LevenshteinSimilarityMeasurer {
    fun measure(lhs: IntArray, rhs: IntArray): Int {
        val lhsLength = lhs.size
        val rhsLength = rhs.size

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        (1..rhsLength).forEach { i ->
            newCost[0] = i
            (1..lhsLength).forEach { j ->
                val editCost = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + editCost
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = minOf(costInsert, costDelete, costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength]
    }
}
