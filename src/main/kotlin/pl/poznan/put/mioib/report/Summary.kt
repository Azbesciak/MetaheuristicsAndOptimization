package pl.poznan.put.mioib.report

import com.google.gson.Gson
import pl.poznan.put.mioib.model.SolutionProposal
import java.io.File
import java.lang.Exception
import java.util.*

data class Summary(
        val name: String,
        val time: Double,
        val scoreEntries: MutableList<Score> = mutableListOf()
) {



    fun addEntry(solution: SolutionProposal, attempt: Int, time: Double){
        this.scoreEntries.add(Score(solution.score, attempt, time))
    }

    fun save(dir: String="./summary"): String? {
        return try {
            val gson = Gson()
            val jsonString = gson.toJson(this)
            val path = "$dir/${this.name}_${Date().toString()}.result"

            File(path).writeText(jsonString)
            path
        }
        catch (e: Exception){
            print("Error: Couldn't save Summary\n$e")
            null
        }
    }
}
