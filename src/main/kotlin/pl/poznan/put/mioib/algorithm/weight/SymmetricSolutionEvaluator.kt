package pl.poznan.put.mioib.algorithm.weight


class SymmetricSolutionEvaluator(
        private val weightMatrix: WeightMatrix
): SolutionEvaluator {
    override fun solution(sequence: IntArray): Double {
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

    override fun delta(firstIndex: Int, secondIndex: Int, sequence: IntArray): Double {
        var fi = firstIndex
        var si = secondIndex
        val lastIndex = sequence.size - 1
        if ((fi > si && !(fi == lastIndex && si == 0)) || (fi == 0 && si == lastIndex)) {
            fi = secondIndex
            si = firstIndex
        }
        val firstId = sequence[fi]
        val beforeFirstId = sequence[if (fi == 0) lastIndex else (fi - 1)]
        val secondId = sequence[si]
        val afterSecondId = sequence[if (si == lastIndex) 0 else (si + 1)]
        var delta = -weightMatrix[beforeFirstId, firstId]
        delta += weightMatrix[beforeFirstId, secondId]
        delta -= weightMatrix[secondId, afterSecondId]
        delta += weightMatrix[firstId, afterSecondId]
        if (si - fi > 1) {
            val afterFirstId = sequence[fi + 1]
            delta -= weightMatrix[firstId, afterFirstId]
            delta += weightMatrix[secondId, afterFirstId]
            val beforeSecondId = sequence[si - 1]
            delta -= weightMatrix[beforeSecondId, secondId]
            delta += weightMatrix[beforeSecondId, firstId]
        }
        return delta
    }
}
