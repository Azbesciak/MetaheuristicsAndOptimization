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
    fun save() {
        try {
            val gson = Gson()
            val jsonString = gson.toJson(this)

            File("${this.name}_${Date().toString()}.result").writeText(jsonString)
        }
        catch (e: Exception){
            print("Error: Couldn't save Summary\n$e")
        }
    }
}
