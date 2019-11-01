package pl.poznan.put.mioib

import pl.poznan.put.mioib.algorithm.mutators.MergedMutator
import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.GreedyNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.ls.LocalSearchMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.SteepestNeighbourhoodBrowser
import pl.poznan.put.mioib.algorithm.mutators.ls.UpperTriangleNeighbourhoodExplorer
import pl.poznan.put.mioib.algorithm.mutators.nearestneighbor.NearestNeighborMutator
import pl.poznan.put.mioib.algorithm.mutators.random.RandomMutator
import pl.poznan.put.mioib.algorithm.stopcondition.DisabledStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.IterationsCountStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.NotImprovingSolutionStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.StopCondition
import pl.poznan.put.mioib.algorithm.weight.*
import pl.poznan.put.mioib.benchmark.LevenshteinSimilarityMeasurer
import pl.poznan.put.mioib.benchmark.measureTime
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Progress
import pl.poznan.put.mioib.model.SolutionProposal
import pl.poznan.put.mioib.parser.BestScoresReader
import pl.poznan.put.mioib.parser.InstanceParser
import pl.poznan.put.mioib.parser.SolutionParser
import pl.poznan.put.mioib.reader.InstanceSolution
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
    val neighbourhoodExplorer = UpperTriangleNeighbourhoodExplorer
    instances.forEach {
        val random = Random(randomSeed)
        val lsBrowser = GreedyNeighbourhoodBrowser(neighbourhoodExplorer, LOWER_SOLUTION_VALUE)
        val stBrowser = SteepestNeighbourhoodBrowser(neighbourhoodExplorer, LOWER_SOLUTION_VALUE)

        val instance = it.instance
        val weightMatrix = SymmetricWeightMatrix(instance, Euclides2DWeightCalculator)
        val evaluator = SymmetricSolutionEvaluator(weightMatrix)
        val isBetter = MIN_SOLUTION
        arrayOf(
                "Random" to { randomMut(random) },
                "Heuristic" to { heuristic(weightMatrix, random) },
                "Greedy" to { greedyLs(lsBrowser, random, isBetter) },
                "Steepest" to { steepestLs(stBrowser, random, isBetter) }
        ).forEach { (mutatorName, mutatorFactory) ->
            val collectedResults = mutableListOf<Pair<SolutionProposal, Progress>>()
            val averageTime = measureTime(minRetries, warmUp, minDuration, showProgress) {
                val (mutator, stopCondition) = mutatorFactory()
                val result = solve(isBetter, instance, evaluator, mutator, dumpInterval, stopCondition)
                if (collectedResults.size < solutionsToCollect)
                    collectedResults += result
            }
            notifyResult(collectedResults, printer, it, averageTime, mutatorName)
        }
    }
}.main(args)

private fun randomMut(random: Random) = RandomMutator(random, 1) to onceSC()

private fun Params.steepestLs(stBrowser: SteepestNeighbourhoodBrowser, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(stBrowser) prependWithRandom random to notImprovingSC(isBetter).skipFirstCheck

private fun Params.greedyLs(lsBrowser: GreedyNeighbourhoodBrowser, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(lsBrowser) prependWithRandom random to notImprovingSC(isBetter).skipFirstCheck

private fun heuristic(weightMatrix: SymmetricWeightMatrix, random: Random) =
        NearestNeighborMutator(weightMatrix, LOWER_OR_EQUAL_SOLUTION_VALUE) { random.nextInt(it) } to onceSC()

private infix fun SolutionMutator.prependWithRandom(random: Random) =
        MergedMutator(RandomMutator(random, 1), this)

private val StopCondition.skipFirstCheck
    get() = DisabledStopCondition(1, this)

private fun notifyResult(
        collectedResults: List<Pair<SolutionProposal, Progress>>,
        printer: SolutionPrinter,
        instanceSolution: InstanceSolution,
        averageTime: Double,
        algorithm: String
) {
    val stats = collectedResults.stream().mapToDouble { it.first.score }.summaryStatistics()
    val quality = qualityStatistics(collectedResults, instanceSolution)
    val bestScore = instanceSolution.solution.score
    printer.update(
            instanceSolution.name, algorithm,
            averageTime,
            stats.average, stats.min, stats.max,
            bestScore,
            quality.min, quality.average
    )

    val attempts = collectedResults.map { (solution, progress) ->
        Attempt(solution.score, progress.steps)
    }
    val score = Score(stats.average, stats.min, stats.max, bestScore)
    val summary = Summary(instanceSolution.name, algorithm, averageTime, score, attempts)
    summary.save()
}

private fun qualityStatistics(collectedResults: List<Pair<SolutionProposal, Progress>>, instanceSolution: InstanceSolution) =
        collectedResults
                .map { LevenshteinSimilarityMeasurer.measure(it.first.sequence, instanceSolution.solution.sequence) }
                .stream()
                .mapToDouble { it.toDouble() }
                .summaryStatistics()

private fun solve(
        isBetter: SolutionComparator,
        instance: Instance,
        evaluator: SymmetricSolutionEvaluator,
        mutator: SolutionMutator,
        dumpInterval: Int,
        stopConditions: StopCondition
) = Solver.solve(
        instance = instance,
        stopCondition = stopConditions,
        evaluator = evaluator,
        isBetter = isBetter,
        mutator = mutator,
        progressDumpInterval = dumpInterval
)

private fun Params.notImprovingSC(isBetter: SolutionComparator) =
        NotImprovingSolutionStopCondition(notImprovingSolutions, isBetter)

private fun onceSC() = IterationsCountStopCondition(1)
