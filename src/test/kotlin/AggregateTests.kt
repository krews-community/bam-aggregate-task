import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import step.aggregate
import util.*

class AggregateTests {

    @Test
    fun `Run aggregation on a chromosome without reads`() {
        val regions = listOf(
            Region("chr1", 100, 101, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false)
        assertThat(values).isEqualTo(listOf(listOf(0.0F, 0.0F), listOf()))
    }

    @Test
    fun `Run aggregation on a region without reads`() {
        val regions = listOf(
            Region("chr22", 100, 101, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false)
        assertThat(values).isEqualTo(listOf(listOf(0.0F, 0.0F), listOf()))
    }

    @Test
    fun `Run aggregation on regions with reads`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false)
        assertThat(values).isEqualTo(listOf(listOf(1.0F, 0.0F), listOf()))
    }

    @Test
    fun `Run aggregation on regions with reads and different strands`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false)
        assertThat(values).isEqualTo(listOf(listOf(0.5F, 0.5F), listOf()))
    }

    @Test
    fun `Run stranded aggregation`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, true)
        assertThat(values).isEqualTo(listOf(listOf(0.5F, 0.0F), listOf(0.5F, 0.0F)))
    }

}
