package application.model.records.services;

import application.hibernate.HibernateUtil;
import application.model.records.Record;
import application.model.settings.Settings;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component("recordRepository")
public class RecordRepository implements IRecordRepository {

    @Override
    public List<Float> getLatestPriceRecordFor(String targetName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Record order by invoiceDate desc" );
        List<Record> records = query.list();

         List<Float> latestInvoicePrices = records
                 .stream()
                 .filter(
                         x -> x.getProductName().equals(targetName)
                 )
                 .map(x -> x.getInvoicePrice())
                 .limit(3)
                 .collect(Collectors.toList());

        System.out.println(latestInvoicePrices);
        return latestInvoicePrices;
    }

    @Override
    public void Store(ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        StoreRecords(session, prices, productNames, invoiceDate);

        try {
            session.getTransaction().commit();
            HibernateUtil.shutdown();
        } catch (HibernateException ex){
            ex.printStackTrace();
        }
    }

    private void StoreRecords(Session session, ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate){

        for( int i =0; i < prices.size(); i++){
            Record record = new Record();
            record.setInvoiceDate(invoiceDate);
            record.setInvoicePrice(prices.get(i));
            record.setProductName(productNames.get(i));
            System.out.println(record.getProductName()+", "+record.getInvoicePrice()+", " + record.getInvoiceDate());
            session.save(record);
        }
    }

}



