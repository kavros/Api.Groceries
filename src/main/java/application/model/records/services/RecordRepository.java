package application.model.records.services;

import application.hibernate.HibernateUtil;
import application.model.records.Record;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component("historyRepository")
public class RecordRepository implements IRecordRepository {

    @Override
    public LinkedList<Float> getLastThreeInvoicePricesFor(String name) {


        return null;
    }


    @Override
    public void Store(ArrayList<Float> prices, ArrayList<String> productNames, Timestamp invoiceDate) {

        //assert (prices.length == productNames.length);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Record");
        List<Record> records = query.list();


        for( int i =0; i < prices.size(); i++){
            Record record = new Record();
            record.setInvoiceDate(invoiceDate);
            record.setInvoicePrice(prices.get(i));
            record.setProductName(productNames.get(i));
            System.out.println(record.getProductName()+", "+record.getInvoicePrice()+", " + record.getInvoiceDate());
            session.save(record);
        }


        session.getTransaction().commit();
        HibernateUtil.shutdown();
    }


}



