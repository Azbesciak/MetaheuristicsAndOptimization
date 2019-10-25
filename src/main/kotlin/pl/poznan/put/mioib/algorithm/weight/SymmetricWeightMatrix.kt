package pl.poznan.put.mioib.algorithm.weight

import pl.poznan.put.mioib.model.Instance

class SymmetricWeightMatrix(
        private val instance: Instance,
        calculator: EdgeWeightCalculator
) : WeightMatrix {
    private val matrix = DoubleArray(instance.size * instance.size)

    init {
        instance.locations.forEachIndexed { i, from ->
            (i + 1 until instance.size).forEach { j ->
                val to = instance.locations[j]
                val value = calculator.calculate(from, to)
                matrix[i * instance.size + j] = value
                matrix[j * instance.size + i] = value
            }
        }
    }

    // It is about 2x faster than checking whether from < to and then make an index,
    // because of that we need to assign twice.
    // But this assignment does not cost much - about 5% longer even for 5k locations on create time.
    override fun get(from: Int, to: Int) = matrix[from * instance.size + to]

}
