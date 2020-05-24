package application.model.history.services;


import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;

public interface IRecordRepository {

    LinkedList<Float> getLastThreeInvoicePricesFor(String name);

    void Store(Float[] prices, String[] productNames, Date invoiceDate);
}
