package step

import util.*
import java.nio.file.Path
import java.io.File
import htsjdk.samtools.*
import mu.KotlinLogging
import kotlin.math.roundToInt
import java.nio.file.Files
import kotlin.math.max
import kotlin.math.abs

private val log = KotlinLogging.logger {}

/**
 * Aggregates alignments over a sequence of genomic regions.
 * @param regions the regions to aggregate.
 * @param alignments path to BAM file from which to read the alignments.
 */
fun barcodeMap(regions: List<Region>, alignments: Path, forwardShift: Int = 0, reverseShift: Int = 0, strand: String? = null): Map<String, Map<String, Int>> {

    val queryExtension = max(abs(forwardShift), abs(reverseShift)) + 1

    SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS).open(alignments.toFile()).use {
        if (SamFiles.findIndex(alignments) == null)
            BAMIndexer.createIndex(it, alignments.resolveSibling("${alignments.fileName}.bai"))
    }

    val results: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    SamReaderFactory.make().validationStringency(ValidationStringency.SILENT).open(alignments.toFile()).use {

        regions.forEachIndexed { i, region ->

            it.query(region.chromosome, region.start - queryExtension, region.end + queryExtension, false).use { q ->
                q.forEach { alignment ->
                    if (strand !== null) {
                        if (strand == "forward" && ((region.strand == '-') != alignment.readNegativeStrandFlag)) return@forEach
                        if (strand == "reverse" && ((region.strand == '-') == alignment.readNegativeStrandFlag)) return@forEach
                    }
                    val start = pileUpStart(alignment, forwardShift, reverseShift)
                    if (start >= region.start && start <= region.end) {
                        val barcode = alignment.getAttribute("CB")?.toString()
                        if (barcode === null) return@forEach
                        if (!results.containsKey(barcode)) results[barcode] = mutableMapOf()
                        if (!results.get(barcode)!!.containsKey(region.name)) results.get(barcode)!![region.name] = 0
                        results.get(barcode)!![region.name] = results.get(barcode)!!.get(region.name)!! + 1
                    }
                }
            }

            if (i % 1000 == 0) log.info {
                "aggregating region $i of ${regions.size} (${(i.toFloat() / regions.size * 100).roundToInt()}%)"
            }

        }

    }

    return results

}

fun barcodeMatrix(regions: List<Region>, alignments: Path, outputFile: Path, forwardShift: Int = 0, reverseShift: Int = 0) {
    val results = barcodeMap(regions, alignments, forwardShift, reverseShift)
    val sortedKeys = regions.map { it.name }
    Files.newBufferedWriter(outputFile).use {
        it.write(sortedKeys.joinToString("\t") + "\n")
        results.forEach { k, v ->
            it.write(k + "\t" + sortedKeys.map {
                if (v.containsKey(it)) v.get(it)!!.toString() else "0"
            }.joinToString("\t") + "\n")
        }
    }
}
