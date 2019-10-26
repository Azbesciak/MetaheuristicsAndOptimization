package pl.poznan.put.mioib.algorithm.weight

import pl.poznan.put.mioib.model.SolutionProposal


typealias SolutionComparator = (old: SolutionProposal, newOne: SolutionProposal) -> Boolean

private const val MIN_IMPROVEMENT = 1e-8
val MIN_SOLUTION: SolutionComparator = { old, newOne -> old.score - newOne.score > MIN_IMPROVEMENT }

typealias SolutionValueComparator = (old: Double, newOne: Double) -> Boolean

val MIN_OR_EQUAL_SOLUTION_VALUE: SolutionValueComparator = { old, newOne -> newOne <= old }
