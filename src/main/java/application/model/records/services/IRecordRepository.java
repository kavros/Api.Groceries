package application.model.records.services;


import org.hibernate.HibernateException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface IRecordRepository {

    List<Float> getLatestPriceRecordFor(String name);

    void Store(ArrayList<Float> prices,
               ArrayList<String> productNames,
               Timestamp invoiceDate) throws HibernateException;
}
