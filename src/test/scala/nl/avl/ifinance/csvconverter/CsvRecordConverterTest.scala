package nl.avl.ifinance.csvconverter

import org.scalatest._
import java.io.StringReader
import java.io.StringWriter

class CsvRecordConverterTest extends FlatSpec with ShouldMatchers {

    "A CsvRecordConverter" should "output an empty file if the input is empty" in {
        val reader = new StringReader("")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("")
    }

    it should "give empty output if the records are unparsable" in {
        val reader = new StringReader("some,bogus,input")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("")
    }

    it should "give correctly escape any quotes in the CSV output" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","some<,weird ""quote","","","","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("""20110501,-348.14,987654321,AH Kudelstaart,"some<,weird ""quote"""")
    }

    it should "output multiple records correctly" in {
        val input = """
            |"1","EUR",20110501,"D",348.14,"3","Transaction title1",20110502,"ba","","description1","","","","",""
            |"2","EUR",20110502,"C",300.00,"4","Transaction title2",20110503,"ba","","description2","","","","",""
            |"unparseable"
            |
            |above is empty""".stripMargin.trim
        val reader = new StringReader(input)
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("""
                |20110501,-348.14,3,Transaction title1,description1
                |20110502,300.00,4,Transaction title2,description2
                |""".stripMargin.trim)

    }

    it should "give a correct CSV if the input is parseable" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","Pin-transactie...","","","","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("""20110501,-348.14,987654321,AH Kudelstaart,Pin-transactie...""")
    }

    it should "give concatenate the different 'description' fields" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","desc1","desc2","desc3","desc4","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("""20110501,-348.14,987654321,AH Kudelstaart,"desc1
                |desc2
                |desc3
                |desc4"""".stripMargin.trim)
    }

    it should "give concatenate the different 'description' fields, but trim the newlines" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","","desc2","","","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("""20110501,-348.14,987654321,AH Kudelstaart,desc2""")
    }

    it should "give concatenate the different 'description' fields, but enclosing newlines shouldn't be trimmed" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","desc1","","","desc4","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        val x = """20110501,-348.14,987654321,AH Kudelstaart,"desc1
                |
                |
                |desc4"""".stripMargin.trim
                
        writer.toString.trim should equal("""20110501,-348.14,987654321,AH Kudelstaart,"desc1
                |
                |
                |desc4"""".stripMargin.trim)
    }
    
    it should "not output any quotes when outputting the empty string" in {
        val reader = new StringReader(""""0123456789","EUR",20110501,"D",348.14,"987654321","AH Kudelstaart",20110502,"ba","","","","","","",""""")
        val writer = new StringWriter()

        new CsvRecordConverter(reader, writer).convert

        writer.toString.trim should equal("20110501,-348.14,987654321,AH Kudelstaart,")
    }

}
