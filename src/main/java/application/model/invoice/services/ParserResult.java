package application.model.invoice.services;

import application.model.invoice.InvoiceProduct;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParserResult {
    public Timestamp invoiceDate;
    public List<String> warnings;
    public List<InvoiceProduct> invoiceProducts;

    public ParserResult(){
        warnings = new ArrayList<>();
        invoiceProducts = new ArrayList<>();
    }
}
