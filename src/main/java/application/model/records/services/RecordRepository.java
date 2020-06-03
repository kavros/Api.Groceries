package application.model.records.services;

import application.hibernate.HibernateUtil;
import application.model.records.Record;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component("recordRepository")
public class RecordRepository implements IRecordRepository {


    public Map<String,List<Float>> getLatestPrices(List<String> productNames) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Record order by invoiceDate desc" );
        List<Record> records = query.list();

        Map<String,List<Float>>  map  = new HashMap<>();
        for(String targetName : productNames) {
            List<Float> latestInvoicePrices = records
                    .stream()
                    .filter(
                            x -> x.getProductName().equals(targetName)
                    )
                    .map(x -> x.getInvoicePrice())
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(targetName,latestInvoicePrices);
        }
        return map;
    }


    @Override
    public void Store(ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate) throws HibernateException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        StoreRecords(session, prices, productNames, invoiceDate);

        session.getTransaction().commit();
        HibernateUtil.shutdown();

    }

    private void StoreRecords(Session session, ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate){

        for( int i =0; i < prices.size(); i++){
            Record record = new Record();
            record.setInvoiceDate(invoiceDate);
            record.setInvoicePrice(prices.get(i));
            record.setProductName(productNames.get(i));
            session.save(record);
        }
    }

}



