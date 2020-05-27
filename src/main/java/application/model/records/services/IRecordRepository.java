package application.model.records.services;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

public interface IRecordRepository {

    LinkedList<Float> getLastThreeInvoicePricesFor(String name);

    void Store(ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate);
}
