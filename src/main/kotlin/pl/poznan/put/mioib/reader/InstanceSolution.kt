package pl.poznan.put.mioib.reader

import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Solution

data class InstanceSolution(
        val name: String,
        val instance: Instance,
        val solution: Solution
)
