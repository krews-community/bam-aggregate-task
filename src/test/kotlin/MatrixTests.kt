import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import step.matrix
import step.randomAccessMatrix
import util.*
import java.nio.file.Files
import java.io.File

class MatrixTests {

    @Test
    fun `Run aggregation on a chromosome without reads`() {
        val regions = listOf(
            Region("chr1", 100, 101, "", 0, '+')
        )
        val values = matrix(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(listOf(0, 0)))
    }

    @Test
    fun `Run aggregation on a region without reads`() {
        val regions = listOf(
            Region("chr22", 100, 101, "", 0, '+')
        )
        val values = matrix(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(listOf(0, 0)))
    }

    @Test
    fun `Run aggregation on regions with reads`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = matrix(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(listOf(1, 0), listOf(0, 0)))
    }

    @Test
    fun `Run aggregation on regions with reads and different strands`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = matrix(regions, TEST_BAM_PATH)
        assertThat(values).isEqualTo(listOf(listOf(0, 1), listOf(0, 0)))
    }

    @Test
    fun `Run aggregation on regions with reads on the forward strand only`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "", 0, '+'),
            Region("chr22", 10667451, 10667452, "", 0, '-')
        )
        val values = matrix(regions, TEST_BAM_PATH, 0, 0, "forward")
        assertThat(values).isEqualTo(listOf(listOf(1, 0), listOf(0, 0)))
    }

    @Test
    fun `Run aggregation on regions with reads on the reverse strand only`() {
        val regions = listOf(
            Region("chr22", 10602488, 10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val values = matrix(regions, TEST_BAM_PATH, 0, 0, "reverse")
        assertThat(values).isEqualTo(listOf(listOf(0, 1), listOf(0, 0)))
    }

    @Test
    fun `Run aggregation and write a random access matrix`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val tf = Files.createTempFile("t", "t")
        randomAccessMatrix(regions, TEST_BAM_PATH, tf)
        assertThat(File(tf.toString()).readText()).isEqualTo("[14,14]\n[[0,0],[0,1]]\n[[0,0],[0,0]]\n")
    }

    @Test
    fun `Run aggregation and write a random access matrix with a shift`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val tf = Files.createTempFile("t", "t")
        randomAccessMatrix(regions, TEST_BAM_PATH, tf, 1, 0)
        assertThat(File(tf.toString()).readText()).isEqualTo("[14,14]\n[[0,0],[1,0]]\n[[0,0],[0,0]]\n")
    }

    @Test
    fun `Run aggregation and write a random access matrix with a shift and a small batch size`() {
        val regions = listOf(
            Region("chr22", 10602488,10602489, "", 0, '-'),
            Region("chr22", 10667451, 10667452, "", 0, '+')
        )
        val tf = Files.createTempFile("t", "t")
        randomAccessMatrix(regions, TEST_BAM_PATH, tf, 1, 0, batchSize = 1)
        assertThat(File(tf.toString()).readText()).isEqualTo("[14,14]\n[[0,0],[1,0]]\n[[0,0],[0,0]]\n")
    }

}
