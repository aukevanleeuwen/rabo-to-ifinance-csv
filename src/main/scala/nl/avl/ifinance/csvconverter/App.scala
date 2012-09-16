package nl.avl.ifinance.csvconverter

import java.io.FileReader
import java.io.FileWriter

case class Config(input: String = "", output: String = "", verbose: Boolean = false)

object App {
    def main(args: Array[String]): Unit = {
        val parser = new scopt.immutable.OptionParser[Config]("java -jar <programjar>", "Rabobank to iFinance Converter") {
            def options = Seq(
                booleanOpt("v", "verbose", "Be more verbose") { (v: Boolean, c: Config) => c.copy(verbose = v) },
                arg("<input>", "Input file") { (v: String, c: Config) => c.copy(input = v) },
                arg("<output>", "Output file") { (v: String, c: Config) => c.copy(input = v) }
            )
        }

        parser.parse(args, Config()) map { config =>
            val reader = new FileReader(args(0))
            val writer = new FileWriter(args(1))

            new CsvRecordConverter(reader, writer).convert
        }
    }
}
