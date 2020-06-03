package application.model.smast.services;

import application.hibernate.HibernateUtil;
import application.model.smast.RetailPrices;
import application.model.smast.Smast;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("retailPricesRepository")
public class RetailPricesRepository implements IRetailPricesRepository {


    public Map<String,Smast> getRetailPrices() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Smast");
        Map<String,Smast> retailPrices =
                ((List<Smast>) query.list() )
                        .stream()
                        .collect(Collectors.toMap(x -> x.getsCode(),x ->x));



        session.getTransaction().commit();
        HibernateUtil.shutdown();

        return retailPrices;
    }
}
