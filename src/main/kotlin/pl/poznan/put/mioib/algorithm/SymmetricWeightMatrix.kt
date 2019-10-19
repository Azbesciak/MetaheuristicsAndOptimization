package pl.poznan.put.mioib.algorithm

import pl.poznan.put.mioib.model.Instance

class SymmetricWeightMatrix(
        private val instance: Instance,
        calculator: EdgeWeightCalculator
): WeightMatrix {
    private val matrix = DoubleArray(instance.size * instance.size) {
        val from = it / instance.size
        val to = it % instance.size
        if (from >= to) 0.0
        else calculator.calculate(instance.locations[from], instance.locations[to])
    }

    override fun get(from: Int, to: Int): Double {
        val index = if (from >= to) (to * instance.size + from) else (from * instance.size + to)
        return matrix[index]
    }

}
