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
import java.util.stream.Collectors;

@Component("retailPricesRepository")
public class RetailPricesRepository implements IRetailPricesRepository {

    @Override
    public Float getRetailPrice(String sCode) {
        return getEntryFor(sCode).getsRetailPr();
    }

    private Smast getEntryFor(String sCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Smast");
        Smast entry =
                ((List<Smast>) query.list() )
                        .stream()
                        .filter(x -> x.getsCode().equals(sCode))
                        .collect(Collectors.toList()).get(0);



        session.getTransaction().commit();
        HibernateUtil.shutdown();

        return entry;
    }
}
