package application.model.smast.services;

import application.hibernate.HibernateUtil;
import application.hibernate.IHibernateUtil;
import application.model.smast.Smast;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component("retailPricesRepository")
public class RetailPricesRepository implements IRetailPricesRepository {
    @Autowired
    IHibernateUtil dbConnection;

    public List<Smast> getRetailPrices(List<String> sCodes) {
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        String queryText = "from Smast report " +
                "where report.sCode in :sCodes";

        Query query = session.createQuery(queryText);
        query.setParameterList("sCodes", sCodes);
        List<Smast> smastList = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return smastList;
    }

    public String getSName(String sCode) {
        List<Smast> smastList =  getRetailPrices(Arrays.asList(sCode));
        return smastList.isEmpty()? null: smastList.get(0).getsName();
    }

    public void updatePrices(List<Map.Entry<String, BigDecimal>> sCodesAndPrices) {
        Session session = dbConnection.getSessionFactory().openSession();
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
        session.close();
    }
}
