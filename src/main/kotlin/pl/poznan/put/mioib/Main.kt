package pl.poznan.put.mioib

import pl.poznan.put.mioib.algorithm.mutators.MergedMutator
import pl.poznan.put.mioib.algorithm.mutators.SolutionMutator
import pl.poznan.put.mioib.algorithm.mutators.ls.*
import pl.poznan.put.mioib.algorithm.mutators.nearestneighbor.NearestNeighborMutator
import pl.poznan.put.mioib.algorithm.mutators.random.RandomMutator
import pl.poznan.put.mioib.algorithm.mutators.sa.SimulatedAnnealingMutator
import pl.poznan.put.mioib.algorithm.stopcondition.DisabledStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.IterationsCountStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.NotImprovingSolutionStopCondition
import pl.poznan.put.mioib.algorithm.stopcondition.StopCondition
import pl.poznan.put.mioib.algorithm.weight.*
import pl.poznan.put.mioib.benchmark.measureTime
import pl.poznan.put.mioib.benchmark.similarity.SameSequenceOccurrenceSimilarity
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

typealias Executor = Pair<String, (r: Random) -> Pair<SolutionMutator, StopCondition>>

fun main(args: Array<String>) = ProgramExecutor {
    val solutions = BestScoresReader.read(solutionValuesPath)
    val instances = InstanceSolutionReader(
            InstanceParser,
            SolutionParser { solutions[it] }
    ).read(instancePath)
    val printer = SolutionPrinter()
    printer.init()
    instances.forEach {
        val lsBrowser = GreedyNeighbourhoodBrowser(0.0, { 0 }, LOWER_SOLUTION_VALUE)
        val stBrowser = SteepestNeighbourhoodBrowser(0.0, { 0 }, LOWER_SOLUTION_VALUE)
        val instance = it.instance
        val weightMatrix = SymmetricWeightMatrix(instance, Euclides2DWeightCalculator)
        val evaluator = SymmetricSolutionEvaluator(weightMatrix)
        val isBetter = MIN_SOLUTION
        /**
        ZeroNBStart - neighbourhood is explored from zero
        RandomNBStart - neighbourhood is explored from random value
        ContinuousNBStart - recently returned from is remembered, it is starting point for next iteration
        HeuristicInit - Algorithm is initialized with heuristic solution
        RandomInit - Algorithm is initialized with random solution
         **/
        arrayOf<Executor>(
                "Random" to { r -> randomMut(r, instance) },
                "Heuristic" to { r -> heuristic(weightMatrix, r) },
                "Greedy-ZeroNBStart-RandomInit" to { r -> greedyLs(lsBrowser, r, isBetter) },
                "Greedy-RandomNBStart-RandomInit" to { r -> greedyLs(greedyNB(r), r, isBetter) },
                "Greedy-ZeroNBStart-HeuristicInit" to { r -> greedyLsH(lsBrowser, weightMatrix, r, isBetter) },
                "Greedy-ContinuousNBStart-RandomInit" to { r -> greedyLs(statefulGreedy(), r, isBetter) },
                "Greedy-ContinuousNBStart-HeuristicInit" to { r -> greedyLsH(statefulGreedy(), weightMatrix, r, isBetter) },
                "Steepest-ZeroNBStart-RandomInit" to { r -> steepestLs(stBrowser, r, isBetter) },
                "Steepest-RandomNBStart-RandomInit" to { r -> steepestLs(steepestNB(r), r, isBetter) },
                "Steepest-ZeroNBStart-HeuristicInit" to { r -> steepestLsH(stBrowser, weightMatrix, r, isBetter) },
                "Steepest-ContinuousNBStart-RandomInit" to { r -> steepestLs(statefulSteepest(), r, isBetter) },
                "Steepest-ContinuousNBStart-HeuristicInit" to { r -> steepestLsH(statefulSteepest(), weightMatrix, r, isBetter) },
                "Simulated Annealing" to { r -> simulatedAnnealing(r, weightMatrix) }
        ).forEach { (mutatorName, mutatorFactory) ->
            val random = Random(randomSeed)
            val collectedResults = mutableListOf<Pair<SolutionProposal, Progress>>()
            val averageTime = measureTime(minRetries, warmUp, minDuration, showProgress) {
                val (mutator, stopCondition) = mutatorFactory(random)
                val result = solve(isBetter, instance, evaluator, mutator, dumpInterval, stopCondition)
                if (collectedResults.size < solutionsToCollect)
                    collectedResults += result
            }
            notifyResult(collectedResults, printer, it, averageTime, mutatorName)
        }
    }
}.main(args)

private fun statefulGreedy() = stateful { GreedyNeighbourhoodBrowser(0.0, it, LOWER_SOLUTION_VALUE) }
private fun statefulSteepest() = stateful { SteepestNeighbourhoodBrowser(0.0, it, LOWER_SOLUTION_VALUE) }

private inline fun stateful(f: ((Int) -> Int) -> NeighbourhoodBrowser): StatefulBrowser {
    lateinit var browser: StatefulBrowser
    browser = StatefulBrowser(0, f { browser.currentFrom })
    return browser
}

private fun simulatedAnnealing(random: Random, weightMatrix: SymmetricWeightMatrix) =
        SimulatedAnnealingMutator(RandomNeighbourhoodBrowser(random), random, LOWER_SOLUTION_VALUE) prependWith nearest(weightMatrix, random) to endlessSolutions()

private fun randomMut(random: Random, instance: Instance) =
        RandomMutator(random, instance.size * instance.size / 3) to endlessSolutions()

private fun endlessSolutions(): StopCondition {
    return object : StopCondition {
        override fun shouldStop(solution: SolutionProposal) = false
    }
}

private fun Params.steepestLs(stBrowser: NeighbourhoodBrowser, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(stBrowser) prependWithRandom random to notImprovingSC(isBetter).skipFirstCheck

private fun Params.steepestLsH(stBrowser: NeighbourhoodBrowser, weightMatrix: SymmetricWeightMatrix, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(stBrowser) prependWith nearest(weightMatrix, random) to notImprovingSC(isBetter).skipFirstCheck

private fun greedyNB(random: Random) = GreedyNeighbourhoodBrowser(0.0, { random.nextInt(it) }, LOWER_SOLUTION_VALUE)
private fun steepestNB(random: Random) = SteepestNeighbourhoodBrowser(0.0, { random.nextInt(it) }, LOWER_SOLUTION_VALUE)

private fun Params.greedyLs(lsBrowser: NeighbourhoodBrowser, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(lsBrowser) prependWithRandom random to notImprovingSC(isBetter).skipFirstCheck

private fun Params.greedyLsH(lsBrowser: NeighbourhoodBrowser, weightMatrix: SymmetricWeightMatrix, random: Random, isBetter: SolutionComparator) =
        LocalSearchMutator(lsBrowser) prependWith nearest(weightMatrix, random) to notImprovingSC(isBetter).skipFirstCheck

private fun heuristic(weightMatrix: SymmetricWeightMatrix, random: Random) =
        nearest(weightMatrix, random) to onceSC()

private fun nearest(weightMatrix: SymmetricWeightMatrix, random: Random) =
        NearestNeighborMutator(weightMatrix, LOWER_OR_EQUAL_SOLUTION_VALUE) { random.nextInt(it) }

private infix fun SolutionMutator.prependWith(mutator: SolutionMutator) = MergedMutator(mutator, this)

private infix fun SolutionMutator.prependWithRandom(random: Random) = prependWith(RandomMutator(random, 1))

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
                .map { SameSequenceOccurrenceSimilarity.measure(it.first.sequence, instanceSolution.solution.sequence) }
                .stream()
                .mapToDouble { it }
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

private fun Params.notImprovingSC(isBetter: SolutionComparator, count: Int = 0) =
        NotImprovingSolutionStopCondition(count, isBetter)

private fun onceSC() = IterationsCountStopCondition(1)
