package pl.poznan.put.mioib.parser

import java.io.File

object FileContentParser {
    operator fun <T> invoke(contentName: String, files: List<File>, parser: ContentParser<T>): T {
        val content = files
                .first { it.name.endsWith(parser.extension) }
                .fileContent()
        return parser.parse(contentName, content)
    }

    private fun File.fileContent(): List<String> {
        require(exists()) {
            "file '$this' does not exist"
        }
        require(isFile) {
            "file '$this' is not a file"
        }
        return readLines()
    }
}
