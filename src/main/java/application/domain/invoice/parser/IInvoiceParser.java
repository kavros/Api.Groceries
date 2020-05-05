package application.domain.invoice.parser;

import application.model.invoice.Invoice;

public interface IInvoiceParser {
    Invoice getInvoice(String invoiceContent);
}
