package application.model.records.services;

import application.model.records.Product;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IRecordsRepository {

    List<Product> getProducts(Timestamp timestamp);
    ParserResult parseInvoice(String invoiceContent) throws ParseException ;
    public Map<String,List<Float>> getLatestPrices(List<String> productNames);
}
