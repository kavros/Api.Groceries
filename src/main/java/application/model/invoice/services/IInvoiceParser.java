package application.model.invoice.services;

import application.model.invoice.InvoiceProduct;

import java.util.ArrayList;
import java.util.Date;

public interface IInvoiceParser {
    void parseInvoice(String invoiceContent);
    ArrayList<InvoiceProduct> getProducts();
    Date getDate();

    //String[] getProductNames();
}
