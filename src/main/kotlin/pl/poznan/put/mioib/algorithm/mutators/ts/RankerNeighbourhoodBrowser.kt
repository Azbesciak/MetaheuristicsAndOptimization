package pl.poznan.put.mioib.algorithm.mutators.ts

import pl.poznan.put.mioib.algorithm.mutators.ls.NeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.ls.upperTriangleNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.SolutionValueComparator
import pl.poznan.put.mioib.model.DeltaUpdate

class RankerNeighbourhoodBrowser(
        private val resultMaxSize: Int,
        private val isBetter: SolutionValueComparator,
        private val eliminateCollision: Boolean = true,
        private val seed: (Int) -> Int
) : NeighbourhoodBrowser {
    private val lastIndex = resultMaxSize - 1
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        val res = arrayOfNulls<DeltaUpdate>(resultMaxSize)
        upperTriangleNeighbourhoodBrowser(indices, evaluator, seed) { from, to, delta ->
            shouldReplace(res[lastIndex], delta) {
                val newValue = DeltaUpdate(from, to, delta)
                res[lastIndex] = newValue
                replaceDeeper(newValue, res)
            }
        }
        return res.filterNotNull()
    }

    private inline fun replaceDeeper(value: DeltaUpdate, res: Array<DeltaUpdate?>) {
        var index = lastIndex - 1
        while (index >= 0) {
            val query = res[index]
            shouldReplace(query, value = value.scoreDelta, onFail = {
                if (query != null && query collidesWith value)
                    res[index + 1] = null
                return
            }) {
                res[index + 1] = if (query == null || query collidesWith value) null else query
                res[index] = value
                --index
            }
        }
    }

    private inline infix fun DeltaUpdate.collidesWith(previous: DeltaUpdate) =
            eliminateCollision && (
                    from == previous.from || from == previous.to || to == previous.from || to == previous.to
                    )

    private inline fun shouldReplace(delta: DeltaUpdate?, value: Double, onFail: () -> Unit = {}, onReplace: () -> Unit) {
        if (delta == null || isBetter(delta.scoreDelta, value)) {
            onReplace()
        } else {
            onFail()
        }
    }

}
