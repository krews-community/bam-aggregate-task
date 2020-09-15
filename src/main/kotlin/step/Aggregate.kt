package step

import util.*
import java.nio.file.Path
import htsjdk.samtools.*
import mu.KotlinLogging
import kotlin.math.roundToInt

private val log = KotlinLogging.logger {}

data class StrandedAggregatedReads (
    val forward: List<Float>,
    val reverse: List<Float>?
)

/**
 * Aggregates alignments over a sequence of genomic regions.
 * @param regions the regions to aggregate.
 * @param alignments path to BAM file from which to read the alignments.
 */
fun aggregate(regions: List<Region>, alignments: Path, strandedReads: Boolean, grouped: Boolean): Map<String, StrandedAggregatedReads> {

    fun values(): MutableList<Int> = (regions.first().start..regions.first().end).map { 0 }.toMutableList()
    val forwardResults: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val reverseResults: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val counts: MutableMap<String, Int> = mutableMapOf()

    SamReaderFactory.make().validationStringency(ValidationStringency.SILENT).open(alignments.toFile()).use {

        if (SamFiles.findIndex(alignments) == null)
            BAMIndexer.createIndex(it, alignments.resolveSibling("${alignments.fileName}.bai"))

        regions.forEachIndexed { i, region ->

            val name = if (grouped) region.name else ""
            if (!forwardResults.containsKey(name)) forwardResults[name] = values()
            if (strandedReads && !reverseResults.containsKey(name)) reverseResults[name] = values()

            if (!counts.containsKey(name)) counts[name] = 0
            counts[name] = counts[name]!! + 1

            it.query(region.chromosome, region.start, region.end, false).use { q ->
                q.forEach { alignment ->
                    val start = pileUpStart(alignment)
                    if (start >= region.start && start <= region.end) {
                        val coordinate = if (region.strand == '+') start - region.start else region.end - start
                        val aValues = if (!strandedReads) forwardResults[name] else (
                            if ( (region.strand == '-') == alignment.readNegativeStrandFlag ) forwardResults[name] else reverseResults[name]
                        ) // forward strand if aggregation is unstranded or if the region strand matches the read strand
                        ++aValues!![coordinate]
                    }
                }
            }

            if (i % 1000 == 0) log.info {
                "aggregating region $i of ${regions.size} (${(i.toFloat() / regions.size * 100).roundToInt()}%)"
            }

        }

    }

    return forwardResults.keys.associate { it to
        StrandedAggregatedReads(
            forwardResults.get(it)!!.map { v -> v.toFloat() / counts.get(it)!! },
            reverseResults.get(it)?.map { v -> v.toFloat() / counts.get(it)!! }
        )
    }

}
