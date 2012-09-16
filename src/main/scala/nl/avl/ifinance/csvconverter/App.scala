package nl.avl.ifinance.csvconverter

import java.io.FileReader
import java.io.FileWriter

/**
 * Hello world!
 *
 */
object App {
  def main(args: Array[String]): Unit = {
    val reader = new FileReader(args(0))
    val writer = new FileWriter(args(1))
    
    new CsvRecordConverter(reader, writer).convert
  }
}
