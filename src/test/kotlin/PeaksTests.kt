import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import step.*
import util.Region

class PeaksTests {

    @Test
    fun `Test resizePeaks`() {
        val allPeaks = regions(
            200 to 210, 100 to 110
        )
        val resizedPeaks = resizeRegions(allPeaks, 10)
        val expectedPeaks = regions(195 to 215, 95 to 115)
        assertThat(resizedPeaks).isEqualTo(expectedPeaks)
    }

    @Test
    fun `Test resizePeaks on minus strand regions`() {
        val allPeaks = minusRegions(
            200 to 209, 101 to 110
        )
        val resizedPeaks = resizeRegions(allPeaks, 10)
        val expectedPeaks = minusRegions(195 to 215, 96 to 116)
        assertThat(resizedPeaks).isEqualTo(expectedPeaks)
    }

}

private fun regions(vararg regionPairs: Pair<Int, Int>) = regionPairs.map {
    Region("chr1", it.first, it.second, "", 0, '+')
}

private fun minusRegions(vararg regionPairs: Pair<Int, Int>) = regionPairs.map {
    Region("chr1", it.first, it.second, "", 0, '-')
}
