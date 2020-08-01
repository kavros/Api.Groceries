package application.domain.parser;

import java.text.ParseException;

public interface IInvoiceParser {
    ParserResult parseAndLoad(String invoiceContent) throws ParseException;
}
