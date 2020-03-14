package util

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream

data class Region (
    val chromosome: String,
    val start: Int,
    val end: Int,
    val name: String,
    val score: Int,
    val strand: Char
)

/**
 * Reads a BED 6+ file and processes the reigons it contains with a user-provided function.
 *
 * @param file path to the narrowPeak file to read.
 * @param lineRange if passed, only reads line numbers within this range.
 * @param processRegions function called on the complete set of regions when reading is complete.
 */
fun readBed6File(file: Path, lineRange: IntRange? = null, processRegions: (List<Region>) -> Unit) {
    val rawInputStream = Files.newInputStream(file)
    val inputStream = if (file.toString().endsWith(".gz")) GZIPInputStream(rawInputStream) else rawInputStream
    inputStream.reader().useLines { lines ->
        val regions = lines
            .filterIndexed { index, _ ->
                if (lineRange != null && !lineRange.contains(index)) return@filterIndexed false
                true
            }
            .map { line ->
                val lineParts = line.trim().split("\t")
                Region(
                    chromosome = lineParts[0],
                    start = lineParts[1].toInt(),
                    end = lineParts[2].toInt(),
                    name = lineParts[3],
                    score = lineParts[4].toInt(),
                    strand = lineParts[5][0]
                )
            }.toList()
        processRegions(regions)
    }
}
