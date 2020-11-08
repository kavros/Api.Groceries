package application.model.records.services;

import application.hibernate.HibernateUtil;
import application.model.records.Record;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Component("recordsRepository")
public class RecordsRepository implements IRecordsRepository {

    public void updatePrices(List<Map.Entry<String, BigDecimal>> data, String invoiceDate)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query query = session
                .createQuery("update Record set newPrice= :new_price " +
                        "where name= :product_name and pDate= :invoice_date");
        for(Map.Entry<String, BigDecimal> entry:data) {
            query.setParameter("new_price", entry.getValue());
            query.setParameter("product_name", entry.getKey());
            query.setParameter("invoice_date", invoiceDate);
            query.executeUpdate();
        }
        tx.commit();
        session.close();
    }
    
    public Map<String,List<Float>> getLatestNewPrices(List<String> sCodes) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Record> records = session
                .createQuery("from Record order by pDate desc" )
                .list();
        session.close();

        Map<String,List<Float>>  map  = new HashMap<>();
        for(String sCode : sCodes) {
            List<Float> latestNewPrices = records
                    .stream()
                    .filter(
                            x -> x.getsCode().equals(sCode)
                    )
                    .map(x -> x.getNewPrice().floatValue())
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(sCode,latestNewPrices);
        }
        return map;
    }

    public Map<String, List<Float>> getLatestInvoicePrices(List<String> sCodes) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Record order by pDate desc" );
        List<Record> records = query.list();
        session.close();
        Map<String,List<Float>>  map  = new HashMap<>();
        for(String sCode : sCodes) {
            List<Float> latestInvoicePrices = records
                    .stream()
                    .filter(
                            x -> x.getsCode().equals(sCode)
                    )
                    .map(x -> x.getPrice().floatValue())
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(sCode,latestInvoicePrices);
        }
        return map;
    }
}
