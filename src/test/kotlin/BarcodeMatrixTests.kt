import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import step.barcodeMap
import step.barcodeMatrix
import util.*
import java.nio.file.Files
import java.io.File
import util.readBed6File

class BarcodeMatrixTests {

    @Test
    fun `Run barcode map on test regions`() {
        readBed6File(TEST_BARCODE_BED_PATH) { regions ->
            val map = barcodeMap(regions, TEST_BARCODE_BAM_PATH)
            assertThat(map.keys.size).isEqualTo(810)
        }
    }

    @Test
    fun `Run barcode matrix on test regions`() {
        readBed6File(TEST_BARCODE_BED_PATH) { regions ->
            val tf = Files.createTempFile("t", "t")
            barcodeMatrix(regions, TEST_BARCODE_BAM_PATH, tf, 1, 0)
            assertThat(File(tf.toString()).readText()).isEqualTo(File(TEST_BARCODE_BED_PATH_OUTPUT.toString()).readText())
        }
    }

}
