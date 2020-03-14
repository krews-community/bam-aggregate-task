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
fun aggregate(regions: List<Region>, alignments: Path): List<Int> {

    val values: MutableList<Int> = (regions.first().start..regions.first().end).map { 0 }.toMutableList()

    SamReaderFactory.make().validationStringency(ValidationStringency.SILENT).open(alignments.toFile()).use {

        regions.forEachIndexed { i, region ->

            it.query(region.chromosome, region.start, region.end, false).use { q ->
                q.forEach { alignment ->
                    val start = pileUpStart(alignment)
                    if (start >= region.start && start <= region.end) {
                        val coordinate = if (region.strand == '+') start - region.start else region.end - start
                        ++values[coordinate]
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
