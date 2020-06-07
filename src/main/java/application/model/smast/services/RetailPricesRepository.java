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


    public Map<String,Smast> getRetailPrices(List<String> sCodes) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        //Query query = session.createQuery("from Smast");
        Criteria criteria  = session.createCriteria(Smast.class);
        criteria.add(Restrictions.in("sCode", sCodes.toArray()));
        List<Smast> data = (List<Smast>) criteria.list();

        Map<String,Smast> retailPrices =
                        data
                        .stream()
                        .collect(Collectors.toMap(x -> x.getsCode(),x ->x));



        session.getTransaction().commit();
        HibernateUtil.shutdown();

        return retailPrices;
    }
}
