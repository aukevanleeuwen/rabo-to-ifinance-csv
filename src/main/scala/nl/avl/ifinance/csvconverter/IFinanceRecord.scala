package nl.avl.ifinance.csvconverter

case class IFinanceRecord(
    date: String,
    amount: BigDecimal,
    beneficiary: String,
    title: String,
    description: String)

object IFinanceRecord {

    def toCsvRecord(iFinanceRecord: IFinanceRecord): List[String] = {
        List(
            iFinanceRecord.date,
            iFinanceRecord.amount.toString,
            iFinanceRecord.beneficiary,
            iFinanceRecord.title,
            iFinanceRecord.description
        )
    }
}