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
        val values = aggregate(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(0, 0))
    }

    @Test
    fun `Run aggregation on a region without reads`() {
        val regions = listOf(
            Region("chr22", 100, 101, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(0, 0))
    }

    @Test
    fun `Run aggregation on regions with reads`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(2, 0))
    }

    @Test
    fun `Run aggregation on regions with reads and different strands`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(1, 1))
    }

}
