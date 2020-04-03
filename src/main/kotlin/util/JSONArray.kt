package util

import java.nio.file.Files
import java.nio.file.Path

fun writeJSONArray(values: List<Float>, path: Path) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        writer.write("[${values.joinToString(",")}]\n")
    }
}

fun writeJSONMatrix(values: List<List<Int>>, path: Path) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        writer.write("[${values.joinToString(",") { it.joinToString(",")} }]\n")
    }
}
