package application.domain.invoice.parser;

import application.model.invoice.InvoiceProduct;

import java.util.ArrayList;

public interface IInvoiceParser {
    void parseInvoice(String invoiceContent);
    ArrayList<InvoiceProduct> getProducts();
    //String[] getProductNames();
}
