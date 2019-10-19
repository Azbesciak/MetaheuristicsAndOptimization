package pl.poznan.put.mioib.reader

import pl.poznan.put.mioib.parser.FileContentParser
import pl.poznan.put.mioib.parser.InstanceParser
import pl.poznan.put.mioib.parser.SolutionParser
import java.io.File

object InstanceSolutionReader : Reader<List<InstanceSolution>> {
    override fun read(path: String): List<InstanceSolution> {
        val instanceAndSolutionPath = File(path)
        val fileName = instanceAndSolutionPath.name
        val parentDir = instanceAndSolutionPath.parent
        require(!parentDir.isNullOrBlank()) { "parent dir not found" }
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
                    instance = FileContentParser(name, files, InstanceParser),
                    solution = FileContentParser(name, files, SolutionParser)
            )

    private fun getInstanceFiles(parentDir: String, fileName: String, path: String) =
            requireNotNull(File(parentDir)
                    .listFiles { _, name -> name.startsWith(fileName) }
            ) {
                "instance files for '$path' not found"
            }
}
