package application.model.records.services;

import application.model.records.Record;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface IRecordsRepository {
    List<Record> getProducts(Timestamp timestamp);
    Map<String,List<Float>> getLatestPrices(List<String> productNames);
    void updatePrices(List<Map.Entry<String, Float>> data, String invoiceDate);
}
