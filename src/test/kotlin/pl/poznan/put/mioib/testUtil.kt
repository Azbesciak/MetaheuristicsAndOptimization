package pl.poznan.put.mioib

import com.nhaarman.mockito_kotlin.*
import pl.poznan.put.mioib.algorithm.weight.SolutionEvaluator
import pl.poznan.put.mioib.algorithm.weight.WeightMatrix
import pl.poznan.put.mioib.model.Weighting

data class MatEntry(
        val l1: Int,
        val l2: Int,
        val weight: Number
)

infix fun Pair<Int, Int>.weight(value: Number) = MatEntry(first, second, value)

fun makeSymmetricMockWeightMatrix(vararg entries: MatEntry) = mock<WeightMatrix> {
    fun makePair(from: Int, to: Int, value: Double) {
        on { get(from, to) } doReturn value
        on { get(to, from) } doReturn value
    }
    entries.forEach { makePair(it.l1, it.l2, it.weight.toDouble()) }
}

fun mockEvaluator(vararg items: MatEntry) = mock<SolutionEvaluator> {
    on { delta(any(), any(), any()) } doAnswer {
        val from = it.getArgument<Int>(0)
        val to = it.getArgument<Int>(1)
        (items.firstOrNull { it.l1 == from && it.l2 == to }?.weight ?: 0).toDouble()
    }
}

const val TEST_INSTANCES_ROOT = "./src/test/resources/instances"

class TestInstance(val id: String, val weight: Weighting, instance: String, solution: String) {
    val path = "$TEST_INSTANCES_ROOT/$id"
    val instance = instance.trimIndent().lines()
    val solution = solution.trimIndent().lines()
}

val ULYSSES_16 = TestInstance(
        id = "ulysses16",
        weight = Weighting.GEO,
        instance = """
            NAME: ulysses16.tsp
            TYPE: TSP
            COMMENT: Odyssey of Ulysses (Groetschel/Padberg)
            DIMENSION: 16
            EDGE_WEIGHT_TYPE: GEO
            DISPLAY_DATA_TYPE: COORD_DISPLAY
            NODE_COORD_SECTION
             1 38.24 20.42
             2 39.57 26.15
             3 40.56 25.32
             4 36.26 23.12
             5 33.48 10.54
             6 37.56 12.19
             7 38.42 13.11
             8 37.52 20.44
             9 41.23 9.10
             10 41.17 13.05
             11 36.08 -5.21
             12 38.47 15.13
             13 38.15 15.35
             14 37.51 15.17
             15 35.49 14.32
             16 39.36 19.56
             EOF
            """,
        solution = """
        NAME : ulysses16.opt.tour
        COMMENT : Optimal solution for ulysses16 (6859)
        TYPE : TOUR
        DIMENSION : 16
        TOUR_SECTION
        1 14 13 12 7 6 15 5 11 9 10 16 3 2 4 8
        -1
        EOF
        """
)

val ST_70 = TestInstance(
        id = "st70",
        weight = Weighting.EUC_2D,
        instance = """
            NAME: st70
            TYPE: TSP
            COMMENT: 70-city problem (Smith/Thompson)
            DIMENSION: 70
            EDGE_WEIGHT_TYPE : EUC_2D
            NODE_COORD_SECTION
            1 64 96
            2 80 39
            3 69 23
            4 72 42
            5 48 67
            6 58 43
            7 81 34
            8 79 17
            9 30 23
            10 42 67
            11 7 76
            12 29 51
            13 78 92
            14 64 8
            15 95 57
            16 57 91
            17 40 35
            18 68 40
            19 92 34
            20 62 1
            21 28 43
            22 76 73
            23 67 88
            24 93 54
            25 6 8
            26 87 18
            27 30 9
            28 77 13
            29 78 94
            30 55 3
            31 82 88
            32 73 28
            33 20 55
            34 27 43
            35 95 86
            36 67 99
            37 48 83
            38 75 81
            39 8 19
            40 20 18
            41 54 38
            42 63 36
            43 44 33
            44 52 18
            45 12 13
            46 25 5
            47 58 85
            48 5 67
            49 90 9
            50 41 76
            51 25 76
            52 37 64
            53 56 63
            54 10 55
            55 98 7
            56 16 74
            57 89 60
            58 48 82
            59 81 76
            60 29 60
            61 17 22
            62 5 45
            63 79 70
            64 9 100
            65 17 82
            66 74 67
            67 10 68
            68 48 19
            69 83 86
            70 84 94
            EOF
        """,
        solution = """
            NAME : st70.opt.tour
            COMMENT : Optimal tour for st70 (675) 
            TYPE : TOUR
            DIMENSION : 70
            TOUR_SECTION
            1
            36
            29
            13
            70
            35
            31
            69
            38
            59
            22
            66
            63
            57
            15
            24
            19
            7
            2
            4
            18
            42
            32
            3
            8
            26
            55
            49
            28
            14
            20
            30
            44
            68
            27
            46
            25
            45
            39
            61
            40
            9
            17
            43
            41
            6
            53
            5
            10
            52
            60
            12
            34
            21
            33
            62
            54
            48
            67
            11
            64
            65
            56
            51
            50
            58
            37
            47
            16
            23
            -1
            EOF
            """
)


