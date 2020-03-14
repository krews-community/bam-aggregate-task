package util

import java.nio.file.Files
import java.nio.file.Path

fun writeJSONArray(values: List<Int>, path: Path) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        writer.write("[${values.joinToString { "," }}]\n")
    }
}
