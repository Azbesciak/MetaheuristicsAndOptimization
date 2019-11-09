package pl.poznan.put.mioib.algorithm.mutators.ls

import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.model.DeltaUpdate
import kotlin.random.Random

class RandomNeighbourhoodBrowser(private val random: Random) : NeighbourhoodBrowser {
    override fun browse(indices: IntArray, evaluator: SolutionEvaluator): List<DeltaUpdate> {
        val from = random.nextInt(indices.size)
        while (true) {
            val to = random.nextInt(indices.size)
            if (from == to)
                continue
            return listOf(DeltaUpdate(from, to, evaluator.delta(from, to, indices)))
        }
    }

}
