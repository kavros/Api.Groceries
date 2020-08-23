package application.model.records.services;

import application.hibernate.HibernateUtil;
import application.model.records.Record;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Component("recordsRepository")
public class RecordsRepository implements IRecordsRepository {

    public List<Record> getProducts(Timestamp timestamp) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Record" );
        List<Record> records = query.list();

        return records
                .stream()
                .filter(x->x.getpDate().equals(timestamp))
                .collect(Collectors.toList());
    }


    public void updatePrices(List<Map.Entry<String, Float>> data, String invoiceDate)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query query = session
                .createQuery("update Record set newPrice= :new_price " +
                        "where name= :product_name and pDate= :invoice_date");
        for(Map.Entry<String, Float> entry:data) {

            query.setParameter("new_price", entry.getValue());
            query.setParameter("product_name", entry.getKey());
            query.setParameter("invoice_date", invoiceDate);
            query.executeUpdate();
        }
        tx.commit();
    }


    public Map<String,List<Float>> getLatestPrices(List<String> productNames) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Record order by pDate desc" );
        List<Record> records = query.list();

        Map<String,List<Float>>  map  = new HashMap<>();
        for(String targetName : productNames) {
            List<Float> latestInvoicePrices = records
                    .stream()
                    .filter(
                            x -> x.getName().equals(targetName)
                    )
                    .map(x -> x.newPrice)
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(targetName,latestInvoicePrices);
        }
        return map;
    }


}
