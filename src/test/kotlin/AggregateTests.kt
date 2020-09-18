import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import step.aggregate
import util.*
import step.*

class AggregateTests {

    @Test
    fun `Run aggregation on a chromosome without reads`() {
        val regions = listOf(
            Region("chr1", 100, 101, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, false)
        assertThat(values).isEqualTo(
            mapOf("" to StrandedAggregatedReads(listOf(0.0F, 0.0F), null))
        )
    }

    @Test
    fun `Run aggregation on a region without reads`() {
        val regions = listOf(
            Region("chr22", 100, 101, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, false)
        assertThat(values).isEqualTo(
            mapOf("" to StrandedAggregatedReads(listOf(0.0F, 0.0F), null))
        )
    }

    @Test
    fun `Run aggregation on regions with reads`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, false)
        assertThat(values).isEqualTo(
            mapOf("" to StrandedAggregatedReads(listOf(0.5F, 0.0F), null))
        )
    }

    @Test
    fun `Run aggregation on regions with reads and different strands`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, false)
        assertThat(values).isEqualTo(
            mapOf("" to StrandedAggregatedReads(listOf(0.0F, 0.5F), null))
        )
    }

    @Test
    fun `Run stranded aggregation`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "", 0, '+'),
            Region("chr22", 10667450, 10667451, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, true, false)
        assertThat(values).isEqualTo(
            mapOf("" to StrandedAggregatedReads(listOf(0.5F, 0.0F), listOf(0.5F, 0.0F)))
        )
    }

    @Test
    fun `Run stranded grouped aggregation`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "test1", 0, '+'),
            Region("chr22", 10667451, 10667452, "test1", 0, '+'),
            Region("chr22", 10602488, 10602489, "test2", 0, '+'),
            Region("chr22", 10602488, 10602489, "test2", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, true, true)
        assertThat(values).isEqualTo(
            mapOf(
                "test1" to StrandedAggregatedReads(listOf(0.5F, 0.0F), listOf(0.0F, 0.0F)),
                "test2" to StrandedAggregatedReads(listOf(1.0F, 0.0F), listOf(0.0F, 0.0F))
            )
        )
    }

    @Test
    fun `Run unstranded grouped aggregation`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "test1", 0, '+'),
            Region("chr22", 10667451, 10667452, "test1", 0, '+'),
            Region("chr22", 10602488, 10602489, "test2", 0, '+'),
            Region("chr22", 10667451, 10667452, "test2", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, true)
        assertThat(values).isEqualTo(
            mapOf(
                "test1" to StrandedAggregatedReads(listOf(0.5F, 0.0F), null),
                "test2" to StrandedAggregatedReads(listOf(0.5F, 0.0F), null)
            )
        )
    }

    @Test
    fun `Run shifted aggregation`() {
        val regions = listOf(
            Region("chr22", 10602491, 10602493, "", 0, '-'),
            Region("chr22", 10667445, 10667447, "", 0, '+')
        )
        val values = aggregate(regions, TEST_BAM_PATH, false, true, 4, -5)
        assertThat(values).isEqualTo(
            mapOf(
                "" to StrandedAggregatedReads(listOf(0.5F, 0.5F, 0.0F), null)
            )
        )
    }

}
