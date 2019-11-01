package pl.poznan.put.mioib

import pl.poznan.put.mioib.algorithm.mutators.MergedMutator
import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.GreedyNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.ls.LocalSearchMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.SteepestNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.random.RandomMutator
import pl.poznan.put.mioib.algorithm.stopcondition.AnyStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.NotImprovingSolutionStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.SamePermutationStopCondition
import pl.poznan.put.mioib.algorithm.weight.*
import pl.poznan.put.mioib.benchmark.measureTime
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Progress
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.parser.BestScoresReader
import pl.poznan.put.mioib.parser.InstanceParser
import pl.poznan.put.mioib.parser.SolutionParser
import pl.poznan.put.mioib.reader.InstanceSolutionReader
import pl.poznan.put.mioib.report.Attempt
import pl.poznan.put.mioib.report.Score
import pl.poznan.put.mioib.report.Summary
import pl.poznan.put.mioib.solver.Solver
import kotlin.random.Random

fun main(args: Array<String>) = ProgramExecutor {
    val solutions = BestScoresReader.read(solutionValuesPath)
    val instances = InstanceSolutionReader(
            InstanceParser,
            SolutionParser { solutions[it] }
    ).read(instancePath)
    val printer = SolutionPrinter()
    printer.init()
    instances.forEach {
        val random = Random(randomSeed)
        val lsBrowser = GreedyNeighbourhoodBrowser(MIN_OR_EQUAL_SOLUTION_VALUE)
        val stBrowser = SteepestNeighbourhoodBrowser(MIN_OR_EQUAL_SOLUTION_VALUE)


        val mutators = mutableListOf(
//                LocalSearchMutator(stBrowser) to "Random",
//                LocalSearchMutator(stBrowser) to "Heuristic",
                LocalSearchMutator(lsBrowser) to "Greedy",
                LocalSearchMutator(stBrowser) to "Steepest")

        for (mutator in mutators)
        {
            println("${it.name} (${mutator.second})")
            val instance = it.instance
            val evaluator = SymmetricSolutionEvaluator(SymmetricWeightMatrix(instance, Euclides2DWeightCalculator))
            val isBetter = MIN_SOLUTION
            val collectedResults = mutableListOf<Pair<SolutionProposal, Progress>>()
            val averageTime = measureTime(minRetries, warmUp, minDuration, showProgress) {
                val result = solve(isBetter, instance, evaluator, MergedMutator(RandomMutator(random, 1), mutator.first))
                if (collectedResults.size < solutionsToCollect)
                    collectedResults += result
            }
            val stats = collectedResults.stream().mapToDouble { it.first.score }.summaryStatistics()
            printer.update(it.name, averageTime, stats.average, stats.min, stats.max, solutions[instance.name])

            val summary = Summary("${it.name} (${mutator.second})", averageTime, Score(stats.average, stats.min, stats.max, solutions[instance.name]),
                    collectedResults.map{s -> Attempt(s.first.score, s.second.steps)})
            summary.save()
        }
    }
}.main(args)

private fun Params.solve(
        isBetter: SolutionComparator,
        instance: Instance,
        evaluator: SymmetricSolutionEvaluator,
        mutator: SolutionMutator
): Pair<SolutionProposal, Progress> {
    val stopCondition = prepareStopCondition(isBetter)
    return Solver.solve(
            instance = instance, stopCondition = stopCondition,
            evaluator = evaluator, isBetter = isBetter,
            mutator = mutator
    )
}

private fun Params.prepareStopCondition(isBetter: SolutionComparator) = AnyStopCondition(
        //NotImprovingSolutionStopCondition(notImprovingSolutions, isBetter)
        SamePermutationStopCondition()
)
