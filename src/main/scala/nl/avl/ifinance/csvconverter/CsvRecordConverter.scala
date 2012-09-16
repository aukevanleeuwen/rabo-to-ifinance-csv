package nl.avl.ifinance.csvconverter

import java.io.Reader
import java.io.Writer
import org.supercsv.io.CsvListReader
import org.supercsv.prefs.CsvPreference
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory
import org.supercsv.io.CsvListWriter

class CsvRecordConverter(val reader: Reader, val writer: Writer) {
    lazy val log = LoggerFactory.getLogger(getClass)
    val writerCsvPreferences = new CsvPreference.Builder('"', ',', sys.props("line.separator")).build()

    def convert() = {
        log.debug("Starting conversion.")

        val csvReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE)
        val csvWriter = new CsvListWriter(writer, writerCsvPreferences)

        val inEntries = readCsv(csvReader)
        log.debug("Read {} Rabobank CSV entries.", inEntries.size)

        val rabobankRecords = inEntries.flatMap(RabobankRecord.fromCsvRecord)
        val iFinanceRecords = rabobankRecords.map(toIFinanceRecord)

        val outEntries = iFinanceRecords.map(IFinanceRecord.toCsvRecord)
        log.debug("Writing {} iFinance entries.", outEntries.size)

        writeCsv(csvWriter, outEntries)

        log.debug("Completed conversion.")
    }

    private def readCsv(csvReader: CsvListReader): Seq[List[String]] = {
        def readLine = Option(csvReader.read).map(_.toList)

        Iterator.continually(readLine).takeWhile(_ != None).flatten.toSeq
    }

    private def writeCsv(csvWriter: CsvListWriter, entries: Seq[List[String]]) = {
        try {
            for (entry <- entries) {
                csvWriter.write(bufferAsJavaList(entry.toBuffer))
            }
        } finally {
            try {
                csvWriter.close
            } catch {
                case e => log.warn("Unable to close the CSV writer.", e)
            }
        }

    }

    private def toIFinanceRecord(rabobankRecord: RabobankRecord) = {
        val signedAmount = rabobankRecord.sign match {
            case "D" => rabobankRecord.amount * -1
            case _ => rabobankRecord.amount
        }

        IFinanceRecord(
            date = rabobankRecord.interestDate,
            amount = signedAmount,
            beneficiary = rabobankRecord.targetAccountNumber,
            title = rabobankRecord.targetTitle,
            description = "%s%n%s%n%s%n%s".format(
                rabobankRecord.description1.getOrElse(""),
                rabobankRecord.description2.getOrElse(""),
                rabobankRecord.description3.getOrElse(""),
                rabobankRecord.description4.getOrElse("")
            ).trim
        )
    }
}