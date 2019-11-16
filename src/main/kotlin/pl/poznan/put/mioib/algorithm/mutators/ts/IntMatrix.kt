package pl.poznan.put.mioib.algorithm.mutators.ts

class IntMatrix(val size: Int) {
    val matrix = IntArray(size * size)
    inline operator fun get(from: Int, to: Int) = matrix[from * size + to]

    operator fun set(from: Int, to: Int, value: Int) {
        matrix[from * size + to] = value
    }
}
