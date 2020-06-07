package application.model.invoice.services;

import application.model.invoice.InvoiceProduct;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IInvoiceRepository {

    List<InvoiceProduct> getProducts(Timestamp timestamp);
    ParserResult parseInvoice(String invoiceContent) throws ParseException ;
    public Map<String,List<Double>> getLatestPrices(List<String> productNames);
}
