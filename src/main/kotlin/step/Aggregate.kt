package step

import util.*
import java.nio.file.Path
import htsjdk.samtools.*
import mu.KotlinLogging
import kotlin.math.roundToInt

private val log = KotlinLogging.logger {}

/**
 * Aggregates alignments over a sequence of genomic regions.
 *
 * @param regions the regions to aggregate.
 * @param alignments path to BAM file from which to read the alignments.
 */
fun aggregate(regions: List<Region>, alignments: Path, strandedReads: Boolean): List<List<Float>> {

    val values: MutableList<Int> = (regions.first().start..regions.first().end).map { 0 }.toMutableList()
    val reverseValues: MutableList<Int> = if (strandedReads) (regions.first().start..regions.first().end).map { 0 }.toMutableList() else mutableListOf()

    SamReaderFactory.make().validationStringency(ValidationStringency.SILENT).open(alignments.toFile()).use {

        regions.forEachIndexed { i, region ->

            it.query(region.chromosome, region.start, region.end, false).use { q ->
                q.forEach { alignment ->
                    val start = pileUpStart(alignment)
                    if (start >= region.start && start <= region.end) {
                        val coordinate = if (region.strand == '+') start - region.start else region.end - start
                        val aValues = if (!strandedReads) values else (
                            if ( (region.strand == '-') == alignment.readNegativeStrandFlag ) values else reverseValues
                        ) // forward strand if aggregation is unstranded or if the region strand matches the read strand
                        ++aValues[coordinate]
                    }
                }
            }

            if (i % 1000 == 0) log.info {
                "aggregating region $i of ${regions.size} (${(i.toFloat() / regions.size * 100).roundToInt()}%)"
            }

        }

    }

    return listOf(
        values.map { it.toFloat() / regions.size },
        reverseValues.map { it.toFloat() / regions.size }
    )

}
