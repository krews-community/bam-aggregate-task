package util

import java.nio.file.Files
import java.nio.file.Path

fun writeJSONArray(values: List<List<Float>>, path: Path, strandedReads: Boolean) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        if (!strandedReads)
            writer.write("[${values[0].joinToString(",")}]\n")
        else
            writer.write("{\"forward\":[${values[0].joinToString(",")}],\"reverse\":[${values[1].joinToString(",")}]}")
    }
}

fun writeJSONMatrix(values: List<List<Int>>, path: Path) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        writer.write("[${values.joinToString(",") { "[${it.joinToString(",")}]" } }]\n")
    }
}
