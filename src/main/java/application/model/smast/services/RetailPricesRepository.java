package application.model.smast.services;

import application.hibernate.HibernateUtil;
import application.model.smast.Smast;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("retailPricesRepository")
public class RetailPricesRepository implements IRetailPricesRepository {


    public Map<String,Smast> getRetailPrices(List<String> sCodes) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria criteria  = session.createCriteria(Smast.class);
        criteria.add(Restrictions.in("sCode", sCodes.toArray()));
        List<Smast> data = (List<Smast>) criteria.list();

        Map<String,Smast> retailPrices =
                        data
                        .stream()
                        .collect(Collectors.toMap(x -> x.getsCode(),x ->x));



        session.getTransaction().commit();


        return retailPrices;
    }


    public void updatePrices(List<Map.Entry<String, BigDecimal>> sCodesAndPrices) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session
                .createQuery("update Smast set sRetailPr= :new_price " +
                        "where sCode= :product_sCode");

        for(Map.Entry<String, BigDecimal> entry:sCodesAndPrices) {
            String sCode =  entry.getKey();

            query.setParameter("new_price", entry.getValue());
            query.setParameter("product_sCode", sCode);
            query.executeUpdate();
        }
        tx.commit();
    }
}
