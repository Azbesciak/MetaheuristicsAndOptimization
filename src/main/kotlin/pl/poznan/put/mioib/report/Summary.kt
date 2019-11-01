package pl.poznan.put.mioib.report

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pl.poznan.put.mioib.model.SolutionProposal
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.util.*

data class Summary(
        val name: String,
        val type: String,
        val averageTime: Double,
        val score: Score,
        val attempts: List<Attempt> = listOf()
)
