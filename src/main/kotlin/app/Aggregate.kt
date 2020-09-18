package app

import com.github.ajalt.clikt.core.CliktCommand
import mu.KotlinLogging
import step.aggregate
import step.resizeRegions
import util.AGGREGATE_TSV_SUFFIX
import util.readBed6File
import util.writeJSONArray
import java.nio.file.Path
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path

private val log = KotlinLogging.logger {}

class Aggregate : CliktCommand() {

    private val regionFiles by option("--region-file", help = "paths to region files in BED 6+ format")
        .path(exists = true).multiple()
    private val alignments by option("--alignments", help = "path to alignments in BAM format")
        .path(exists = true).required()
    private val strandedReads by option("--stranded-reads", help = "if set, aggregates reads separately on the plus and minus strands")
        .flag()
    private val grouped by option("--grouped", help = "if set, groups aggregated reads by region name")
        .flag()
    private val expansionSize by option("--expansionSize", help = "number of basepairs by which to expand each region")
        .int().default(2000)
    private val outputDir by option("--output-dir", help = "path to write output")
        .path().required()
    private val forwardShift by option("--forward-shift", help = "if set, shifts forward strand reads by the given number of basepairs")
        .int().default(0)
    private val reverseShift by option("--reverse-shift", help = "if set, shifts reverse strand reads by the given number of basepairs")
        .int().default(0)

    override fun run() {
        runTask(regionFiles, alignments, expansionSize, outputDir, strandedReads, grouped, forwardShift, reverseShift)
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
private fun runTask(
    regionFiles: List<Path>, alignments: Path, expansionSize: Int, outputDir: Path, strandedReads: Boolean, grouped: Boolean,
    forwardShift: Int = 0, reverseShift: Int = 0
) {

    regionFiles.forEach {

        log.info { "Running alignment aggregation for $it" }

        readBed6File(it) { regions ->
            val combinedOutPrefix = "${it.fileName.toString().split(".").first()}_${alignments.fileName.toString().split(".").first()}"
            writeJSONArray(
                aggregate(resizeRegions(regions, expansionSize / 2), alignments, strandedReads, grouped, forwardShift, reverseShift),
                outputDir.resolve("$combinedOutPrefix$AGGREGATE_TSV_SUFFIX"),
                grouped
            )
        }

    }

}
