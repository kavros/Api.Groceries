package application.domain.current.prices;

import application.hibernate.HibernateUtil;
import application.model.smast.CurrentProductPrices;
import application.model.smast.Smast;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component("currentPricesRepository")
public class CurrentPricesRepository implements ICurrentPricesRepository {
    HashMap<String,Smast> prices;

    @Override
    public CurrentProductPrices loadCurrentPrices(List<String> sCodes) {
        System.out.println(sCodes);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(Smast.class);
        criteria.add(Restrictions.in("sCode", sCodes.toArray()));
        List<Smast> data = (List<Smast>) criteria.list();

        CurrentProductPrices currentPrices = new CurrentProductPrices(data);
        prices = new HashMap<>();
        data.forEach( x -> {
            prices.put(x.getsCode(), x);
        });

        session.getTransaction().commit();
        HibernateUtil.shutdown();

        return currentPrices;
    }

    @Override
    public Float getCurrentPrice(String sCode) {
        return prices.get(sCode).getsRetailPr();
    }
}
