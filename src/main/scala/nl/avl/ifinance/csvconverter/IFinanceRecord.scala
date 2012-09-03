package nl.avl.ifinance.csvconverter

case class IFinanceRecord(
    date: String,
    amount: BigDecimal,
    beneficiary: String,
    title: String,
    description: String)

object IFinanceRecord {

    def toCsvRecord(iFinanceRecord: IFinanceRecord): Array[String] = {
        Array(
            iFinanceRecord.date,
            iFinanceRecord.amount.toString,
            iFinanceRecord.beneficiary,
            iFinanceRecord.title,
            iFinanceRecord.description
        )
    }
}