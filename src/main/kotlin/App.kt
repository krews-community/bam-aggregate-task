import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.*
import mu.KotlinLogging
import step.*
import util.*
import java.nio.file.*

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) = Cli().main(args)

class Cli : CliktCommand() {

    private val regionFiles by option("--region-file", help = "paths to region files in BED 6+ format")
        .path(exists = true).multiple()
    private val alignments by option("--alignments", help = "path to alignments in BAM format")
        .path(exists = true).required()
    private val expansionSize by option("--expansionSize", help = "number of basepairs by which to expand each region")
        .int().default(2000)
    private val outputDir by option("--output-dir", help = "path to write output")
        .path().required()

    override fun run() {
        runTask(regionFiles, alignments, expansionSize, outputDir)
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
fun runTask(regionFiles: List<Path>, alignments: Path, expansionSize: Int, outputDir: Path) {

    log.info {
        """
            Running alignment aggregation for:
            BED files: $regionFiles
            BAM file: $alignments
            expansionSize: $expansionSize
        """.trimIndent()
    }

    regionFiles.forEach {
        readBed6File(it) { regions ->
            val combinedOutPrefix = "${it.fileName.toString().split(".").first()}_${alignments.fileName.toString().split(".").first()}"
            writeJSONArray(
                aggregate(resizeRegions(regions, expansionSize / 2), alignments),
                outputDir.resolve("$combinedOutPrefix$AGGREGATE_TSV_SUFFIX")
            )
        }
    }

    log.info { "Done" }

}
