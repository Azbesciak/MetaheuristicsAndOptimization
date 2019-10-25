package pl.poznan.put.mioib.model

import com.google.gson.Gson
import java.io.File
import java.lang.Exception
import java.util.*

data class Summary(
    val name: String,
    val scoreEntries: MutableList<Score>?,
    val time: Double
) {
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
