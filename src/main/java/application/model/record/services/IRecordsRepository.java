package application.model.record.services;

import application.domain.importer.parser.Product;
import application.model.record.Record;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface IRecordsRepository {
    Map<String, List<Float>> getLatestInvoicePrices(List<String> sCodes);
    Map<String,List<Float>> getLatestNewPrices(List<String> productNames);
    void updatePrices(List<Map.Entry<String, BigDecimal>> pNameToPrice, String invoiceDate);
    void storeRecords(List<Record> records, Timestamp invoiceDate);
    List<Record> buildAndGetRecords(List<Product> products, Timestamp timestamp);
}
