package pl.poznan.put.mioib.reader

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.poznan.put.mioib.ST_70
import pl.poznan.put.mioib.model.Instance
import pl.poznan.put.mioib.model.Solution
import pl.poznan.put.mioib.model.Weighting
import pl.poznan.put.mioib.parser.ContentParser

class InstanceSolutionReaderTest {
    @Test
    fun checkCorrectRead() {
        val instanceParserMock = mock<ContentParser<Instance>> {
            on { extension } doReturn "tsp"
            on { parse(any(), any()) } doReturn Instance("", Weighting.EUC_2D, emptyList())
        }
        val solutionParserMock = mock<ContentParser<Solution>> {
            on { extension } doReturn "opt.tour"
            on { parse(any(), any()) } doReturn Solution("", intArrayOf(), 0.0)
        }
        val instanceAndSolution = InstanceSolutionReader(instanceParserMock, solutionParserMock).read(ST_70.path)
        assertEquals(1, instanceAndSolution.size) {
            "expected exactly one instance - solution and instance files"
        }
    }

}
