package pl.poznan.put.mioib.algorithm.weight

import pl.poznan.put.mioib.model.SolutionProposal


typealias SolutionComparator = (old: SolutionProposal, newOne: SolutionProposal) -> Boolean

val MIN_SOLUTION: SolutionComparator = { old, newOne -> newOne.score < old.score }

typealias SolutionValueComparator = (old: Double, newOne: Double) -> Boolean

val MIN_OR_EQUAL_SOLUTION_VALUE: SolutionValueComparator = { old, newOne -> newOne <= old }
