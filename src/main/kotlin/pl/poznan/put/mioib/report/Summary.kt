package pl.poznan.put.mioib.report

import com.google.gson.Gson
import pl.poznan.put.mioib.model.SolutionProposal
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.util.*

data class Summary(
        val name: String,
        val averageTime: Double,
        val score: Score,
        val attempts: List<Double> = listOf<Double>()
) {



    fun save(dir: String="summary"): String? {
        return try {
            val gson = Gson()
            val jsonString = gson.toJson(this)
            val filename = "${this.name}.sum"

            val directory = File(dir)
            directory.mkdirs()

            File(directory, filename).writeText(jsonString)
            "$directory/$filename"
        }
        catch (e: Exception){
            print("Error: Couldn't save Summary\n$e")
            null
        }
    }
}