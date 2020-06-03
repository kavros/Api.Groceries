package application.model.records.services;


import org.hibernate.HibernateException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IRecordRepository {

    Map<String,List<Float>> getLatestPrices(List<String> productNames);

    void Store(ArrayList<Float> prices,
               ArrayList<String> productNames,
               Timestamp invoiceDate) throws HibernateException;
}
