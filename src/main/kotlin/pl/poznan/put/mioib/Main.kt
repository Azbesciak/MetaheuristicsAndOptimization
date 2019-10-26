package pl.poznan.put.mioib

import pl.poznan.put.mioib.algorithm.mutators.MergedMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.GreedyNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.ls.LocalSearchMutator
import pl.poznan.put.mioib.algorithm.mutators.random.RandomMutator
import pl.poznan.put.mioib.algorithm.stopcondition.AnyStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.NotImprovingSolutionStopCondition
import pl.poznan.put.mioib.algorithm.weight.*
import pl.poznan.put.mioib.benchmark.measureTime
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.parser.BestScoresReader
import pl.poznan.put.mioib.parser.InstanceParser
import pl.poznan.put.mioib.parser.SolutionParser
import pl.poznan.put.mioib.reader.InstanceSolutionReader
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
        val instance = it.instance
        val evaluator = SymmetricSolutionEvaluator(SymmetricWeightMatrix(instance, Euclides2DWeightCalculator))
        val isBetter = MIN_SOLUTION
        val lsBrowser = GreedyNeighbourhoodBrowser(MIN_OR_EQUAL_SOLUTION_VALUE)
        val collectedSolutions = mutableListOf<SolutionProposal>()
        val random = Random(randomSeed)
        val averageTime = measureTime(minRetries, warmUp, minDuration, showProgress) {
            val solution = solve(lsBrowser, isBetter, instance, evaluator) { random }
            if (collectedSolutions.size < solutionsToCollect)
                collectedSolutions += solution
        }
        val stats = collectedSolutions.stream().mapToDouble { it.score }.summaryStatistics()
        printer.update(it.name, averageTime, stats.average, stats.min, stats.max, solutions[instance.name])

        val summary = Summary(it.name, averageTime, Score(stats.average, stats.min, stats.max, solutions[instance.name]), collectedSolutions.map{s -> s.score})
        summary.save()
    }
}.main(args)

private fun Params.solve(
        lsBrowser: GreedyNeighbourhoodBrowser,
        isBetter: SolutionComparator,
        instance: Instance,
        evaluator: SymmetricSolutionEvaluator,
        random: () -> Random
): SolutionProposal {
    val mutator = MergedMutator(
            RandomMutator(random(), 1),
            LocalSearchMutator(lsBrowser)
    )
    val stopCondition = prepareStopCondition(isBetter)
    return Solver.solve(
            instance = instance, stopCondition = stopCondition,
            evaluator = evaluator, isBetter = isBetter,
            mutator = mutator
    )
}

private fun Params.prepareStopCondition(isBetter: SolutionComparator) = AnyStopCondition(
        NotImprovingSolutionStopCondition(notImprovingSolutions, isBetter)
)
