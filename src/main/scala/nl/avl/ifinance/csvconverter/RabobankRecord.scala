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
	other1: String,
	description1: String,
	description2: String,
	description3: String,
	description4: String,
	other2: String,
	other3: String	
)

object RabobankRecord {
    private lazy val log = LoggerFactory.getLogger(getClass)

    def fromCsvRecord(data: Array[String]): Option[RabobankRecord] = {
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
                other1 = data(9),
                description1 = data(10),
                description2 = data(11),
                description3 = data(12),
                description4 = data(13),
                other2 = data(14),
                other3 = data(15)
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
