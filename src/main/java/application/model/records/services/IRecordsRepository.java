package application.model.records.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IRecordsRepository {
    Map<String, List<Float>> getLatestInvoicePrices(List<String> sCodes);
    Map<String,List<Float>> getLatestNewPrices(List<String> productNames);
    void updatePrices(List<Map.Entry<String, BigDecimal>> pNameToPrice, String invoiceDate);
}
