package pl.poznan.put.mioib.algorithm

class SolutionEvaluator(
        private val weightMatrix: WeightMatrix
) {
    fun forSolution(sequence: IntArray): Double {
        var result = 0.0
        require(sequence.size >= 2) {
            "instance size must be at least of size 2"
        }
        val start = sequence[0]
        var from = start
        (1 until sequence.size).forEach {
            val to = sequence[it]
            result += weightMatrix[from, to]
            from = to
        }
        result += weightMatrix[from, start]
        return result
    }

    fun delta(firstIndex: Int, secondIndex: Int, sequence: IntArray): Double {
        var fi = firstIndex
        var si = secondIndex
        if (fi > si) {
            fi = secondIndex
            si = firstIndex
        }
        val lastIndex = sequence.size - 1
        val firstId = sequence[fi]
        val beforeFirstId = sequence[if (fi == 0) lastIndex else (fi - 1)]
        val secondId = sequence[si]
        val afterSecondId = sequence[if (si == lastIndex) 0 else (si + 1)]
        var delta = -weightMatrix[beforeFirstId, firstId]
        delta -= weightMatrix[secondId, afterSecondId]
        delta += weightMatrix[beforeFirstId, secondId]
        delta += weightMatrix[firstId, afterSecondId]
        if (si - fi > 1 && !(si == lastIndex && fi == 0)) {
            val afterFirstId = sequence[fi + 1]
            delta -= weightMatrix[firstId, afterFirstId]
            delta += weightMatrix[firstId, afterSecondId]
            val beforeSecondId = sequence[si - 1]
            delta -= weightMatrix[beforeSecondId, secondId]
            delta += weightMatrix[beforeFirstId, secondId]
        }
        return delta
    }
}
