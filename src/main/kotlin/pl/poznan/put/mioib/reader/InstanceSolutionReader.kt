package pl.poznan.put.mioib.reader

import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Solution
import pl.poznan.put.mioib.parser.ContentParser
import pl.poznan.put.mioib.parser.FileContentParser
import java.io.File

class InstanceSolutionReader(
        private val instanceParser: ContentParser<Instance>,
        private val solutionParser: ContentParser<Solution>
) : Reader<List<InstanceSolution>> {
    override fun read(path: String): List<InstanceSolution> {
        val (parentDir, fileName) = getFileSpec(path)
        require(!parentDir.isBlank()) { "parent dir not found" }
        val instanceFiles = getInstanceFiles(parentDir, fileName, path)
        return instanceFiles
                .groupBy { it.instanceName }
                .map { (name, files) -> prepareInstance(name, files) }
    }

    private val File.instanceName
        get() = name.substringBefore(".")

    private fun prepareInstance(name: String, files: List<File>) =
            InstanceSolution(
                    name = name,
                    instance = FileContentParser(name, files, instanceParser),
                    solution = FileContentParser(name, files, solutionParser)
            )

    private fun getInstanceFiles(parentDir: String, fileName: String, path: String) =
            requireNotNull(File(parentDir)
                    .listFiles { _, name -> name.startsWith(fileName) }
            ) {
                "instance files for '$path' not found"
            }

    private fun getFileSpec(path: String) = File(path).run {
        if (isDirectory) path to ""
        else parent to name
    }
}
