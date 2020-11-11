package util

import java.nio.file.*

val TEST_BAM_PATH = getResourcePath("ENCFF375IJW.chr22.bam")
val TEST_BARCODE_BAM_PATH = getResourcePath("GW17_Cortex.10000.bam")
val TEST_BARCODE_BED_PATH = getResourcePath("test.bed6")
val TEST_BARCODE_BED_PATH_OUTPUT = getResourcePath("test.barcode.output.txt")

interface Junk

fun getResourcePath(relativePath: String): Path {
    val url = Junk::class.java.classLoader.getResource(relativePath)
    return Paths.get(url.toURI())
}
