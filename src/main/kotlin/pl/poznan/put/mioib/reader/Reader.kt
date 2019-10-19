package pl.poznan.put.mioib.reader

interface Reader<T> {
    fun read(path: String): T
}
