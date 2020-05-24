package application.model.history.services;

import application.hibernate.HibernateUtil;
import application.model.history.PricesHistory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;


import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

@Component("historyRepository")
public class RecordRepository implements IRecordRepository {

    @Override
    public LinkedList<Float> getLastThreeInvoicePricesFor(String name) {


        return  null;
    }


    @Override
    public void Store(Float[] prices, String[] productNames, Date invoiceDate) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from PricesHistory");
        List<PricesHistory> history = query.list();


        PricesHistory p = new PricesHistory();
        
        //history.add()
        /*history.forEach( x -> {
            if(x.getInvoicePrice() == (float) 1.1)
            {
                System.out.println("wwwwwwwwwwwwwwwwwwwwww");
                x.setInvoicePrice( (float) 1.34);
            }
        });*/


        session.getTransaction().commit();
        HibernateUtil.shutdown();
    }


}
