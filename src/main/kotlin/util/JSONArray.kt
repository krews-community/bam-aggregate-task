package util

import java.nio.file.Files
import java.nio.file.Path

import step.*

fun writeJSONArray(values: Map<String, StrandedAggregatedReads>, path: Path, grouped: Boolean) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        if (grouped)
            writer.write("{" + values.keys.joinToString(",") { 
                val v = values.get(it)!!
                if (v.reverse !== null)
                    "\"$it\":{\"forward\":[${v.forward.joinToString(",")}],\"reverse\":[${v.reverse.joinToString(",")}]}"
                else
                    "\"$it\":[${v.forward.joinToString(",")}]"
            } + "}")
        else if (values.get("") !== null) {
            if (values.get("")!!.reverse !== null)
                writer.write("{\"forward\":[${values.get("")!!.forward.joinToString(",")}],\"reverse\":[${values.get("")!!.reverse!!.joinToString(",")}]")
            else
                writer.write("[${values.get("")!!.forward.joinToString(",")}]")
        }
    }
}

fun writeJSONMatrix(values: List<List<Int>>, path: Path) {
    Files.createDirectories(path.parent)
    Files.newBufferedWriter(path).use { writer ->
        writer.write("[${values.joinToString(",") { "[${it.joinToString(",")}]" } }]\n")
    }
}
