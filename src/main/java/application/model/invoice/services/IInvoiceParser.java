package application.model.invoice.services;

import application.model.invoice.InvoiceProduct;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public interface IInvoiceParser {
    void parseInvoice(String invoiceContent) throws IllegalArgumentException, ParseException;
    ArrayList<InvoiceProduct> getProducts();
    Date getDate();

    //String[] getProductNames();
}
