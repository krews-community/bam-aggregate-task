package app

import com.github.ajalt.clikt.core.CliktCommand
import mu.KotlinLogging
import step.resizeRegions
import util.AGGREGATE_TSV_SUFFIX
import util.readBed6File
import java.nio.file.Path
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import step.matrix
import util.writeJSONMatrix
import step.*

private val log = KotlinLogging.logger {}

class Matrix : CliktCommand() {

    private val regionFiles by option("--region-file", help = "paths to region files in BED 6+ format")
        .path(exists = true).multiple()
    private val alignments by option("--alignments", help = "path to alignments in BAM format")
        .path(exists = true).required()
    private val expansionSize by option("--expansionSize", help = "number of basepairs by which to expand each region")
        .int().default(2000)
    private val outputDir by option("--output-dir", help = "path to write output")
        .path().required()
    private val randomAccess by option("--random-access", help = "if set, writes output in a format designed for seeking and reading specific indexes")
        .flag()
    private val batchSize by option("--batch-size", help = "if passed, gets reads for regions in batches of this size")
        .int().default(10000)
    private val forwardShift by option("--forward-shift", help = "if set, shifts forward strand reads by the given number of basepairs")
        .int().default(0)
    private val reverseShift by option("--reverse-shift", help = "if set, shifts reverse strand reads by the given number of basepairs")
        .int().default(0)

    override fun run() {
        runTask(regionFiles, alignments, expansionSize, outputDir, randomAccess, batchSize, forwardShift, reverseShift)
    }

}

/**
 * Aggregates alignments for BED files.
 *
 * @param regionFiles paths to region files in BED 6+ format
 * @param alignments path to alignment file in BAM format
 * @param expansionSize number of basepairs by which to expand each region around its center
 * @param outputDir path to directory for writing output files
 */
private fun runTask(regionFiles: List<Path>, alignments: Path, expansionSize: Int, outputDir: Path, randomAccess: Boolean, batchSize: Int, forwardShift: Int, reverseShift: Int) {

    regionFiles.forEach {

        log.info { "Running alignment aggregation for $it" }

        readBed6File(it) { regions ->
            val combinedOutPrefix = "${it.fileName.toString().split(".").first()}_${alignments.fileName.toString().split(".").first()}"
            if (randomAccess)
                randomAccessMatrix(
                    resizeRegions(regions, expansionSize / 2), alignments, outputDir.resolve("$combinedOutPrefix$AGGREGATE_TSV_SUFFIX"), forwardShift, reverseShift,
                    batchSize
                )
            else
                writeJSONMatrix(
                    matrix(resizeRegions(regions, expansionSize / 2), alignments, forwardShift, reverseShift),
                    outputDir.resolve("$combinedOutPrefix$AGGREGATE_TSV_SUFFIX")
                )
        }

    }

}
