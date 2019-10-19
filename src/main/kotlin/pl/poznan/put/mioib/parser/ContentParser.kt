package pl.poznan.put.mioib.parser

interface ContentParser<T> {
    val extension: String
    fun parse(contentName: String, content: List<String>): T
}
