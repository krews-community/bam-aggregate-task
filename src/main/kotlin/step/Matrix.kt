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
fun matrix(regions: List<Region>, alignments: Path, forwardShift: Int = 0, reverseShift: Int = 0, strand: String? = null): List<List<Int>> {

    val values: List<MutableList<Int>> = regions.map {
        (it.start..it.end).map { 0 }.toMutableList()
    }
    val queryExtension = max(abs(forwardShift), abs(reverseShift)) + 1

    SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS).open(alignments.toFile()).use {
        if (SamFiles.findIndex(alignments) == null)
            BAMIndexer.createIndex(it, alignments.resolveSibling("${alignments.fileName}.bai"))
    }

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
                        val coordinate = if (region.strand == '+') start - region.start else region.end - start
                        ++values[i][coordinate]
                    }
                }
            }

            if (i % 1000 == 0) log.info {
                "aggregating region $i of ${regions.size} (${(i.toFloat() / regions.size * 100).roundToInt()}%)"
            }

        }

    }

    return values

}

fun randomAccessMatrix(regions: List<Region>, alignments: Path, outputFile: Path, forwardShift: Int = 0, reverseShift: Int = 0, batchSize: Int = 10000) {

    val batches = regions.chunked(batchSize)

    val tf = Files.createTempFile(outputFile.getParent(), "tmp", "tmp")
    var lengths = listOf<Int>()
    Files.newBufferedWriter(tf).use { tfw ->
        lengths = batches.flatMap {
            val forwardLines = matrix(it, alignments, forwardShift, reverseShift, "forward").map { "[${it.joinToString(",")}]" }
            val reverseLines = matrix(it, alignments, forwardShift, reverseShift, "reverse").map { "[${it.joinToString(",")}]" }
            val lines = forwardLines.mapIndexed { i, line -> "[${line},${reverseLines[i]}]\n" }
            tfw.write(lines.joinToString(""))
            lines.map { it.length }
        }
    }
    Files.newBufferedWriter(outputFile).use {
        it.write("[${lengths.joinToString(",")}]\n")
        File(tf.toString()).forEachLine { line ->
            it.write(line + "\n")
        }
    }

}
