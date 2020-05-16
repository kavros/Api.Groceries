package application.domain.invoice.parser;

import application.model.invoice.Invoice;
import application.model.invoice.InvoiceRow;

import java.util.ArrayList;

public interface IInvoiceParser {
    void parseInvoice(String invoiceContent);
    ArrayList<InvoiceRow> getProducts();
    String[] getSnames();
}
