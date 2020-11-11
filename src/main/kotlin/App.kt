import app.Aggregate
import app.Matrix
import app.BarcodeMatrix
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class Cli : CliktCommand() {
    override fun run() {}
}

fun main(args: Array<String>) = Cli().subcommands(Aggregate(), Matrix(), BarcodeMatrix()).main(args)
