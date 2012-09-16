package nl.avl.ifinance.csvconverter

import java.util.Date
import org.slf4j.LoggerFactory

case class RabobankRecord(
	accountNumber: String,
	currency: String,
	interestDate: String,
	sign: String,
	amount: BigDecimal,
	targetAccountNumber: String,
	targetTitle: String,
	transactionDate: String,
	code: String,
	other1: Option[String], // Unsure what this field is for
	description1: Option[String],
	description2: Option[String],
	description3: Option[String],
	description4: Option[String],
	other2: Option[String], // Unsure what this field is for
	other3: Option[String] // Unsure what this field is for	
)

object RabobankRecord {
    private lazy val log = LoggerFactory.getLogger(getClass)

    def fromCsvRecord(data: List[String]): Option[RabobankRecord] = {
        try {
            val entry = RabobankRecord(
                accountNumber = data(0),
                currency = data(1),
                interestDate = data(2),
                sign = data(3),
                amount = BigDecimal(data(4)),
                targetAccountNumber = data(5),
                targetTitle = data(6),
                transactionDate = data(7),
                code = data(8),
                other1 = Option(data(9)),
                description1 = Option(data(10)),
                description2 = Option(data(11)),
                description3 = Option(data(12)),
                description4 = Option(data(13)),
                other2 = Option(data(14)),
                other3 = Option(data(15))
            )
            Some(entry)
        } catch {
            case e => {
                log.error("Unable to parse data into a RabobankEntry: " + Option(data).map(_.toList), e)
                None
            }
        }
    }
}
