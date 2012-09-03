package nl.avl.ifinance.csvconverter

import java.io.Reader
import java.io.Writer
import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.slf4j.LoggerFactory

class CsvRecordConverter(val reader: Reader, val writer: Writer) {
    lazy val log = LoggerFactory.getLogger(getClass)

    def convert() = {
        log.debug("Starting conversion.")

        val csvReader = new CSVReader(reader)
        val csvWriter = new CSVWriter(writer)

        val inEntries = csvReader.readAll.toList
        log.debug("Read {} Rabobank CSV entries.", inEntries.size)

        val rabobankRecords = inEntries.flatMap(RabobankRecord.fromCsvRecord)
        val iFinanceRecords = rabobankRecords.map(toIFinanceRecord)

        val outEntries = iFinanceRecords.map(IFinanceRecord.toCsvRecord)
        log.debug("Writing {} iFinance entries.", outEntries.size)

        try {
            csvWriter.writeAll(bufferAsJavaList(outEntries.toBuffer))
        } finally {
            try {
                csvWriter.close
            } catch {
                case e => log.warn("Unable to close the CSV writer.", e)
            }
        }

        log.debug("Completed conversion.")
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
                rabobankRecord.description1,
                rabobankRecord.description2,
                rabobankRecord.description3,
                rabobankRecord.description4
            ).trim
        )
    }
}