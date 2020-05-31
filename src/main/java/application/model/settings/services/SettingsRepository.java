package application.model.settings.services;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("settingsRepository")
public class SettingsRepository implements ISettingsRepository {



    @Override
    public Float getProfit(String sName) {
        return  getSettingsFor(sName).getProfit();
    }

    @Override
    public Float getMinProfit(String sName) {
        return getSettingsFor(sName).getMinProfit();
    }

    @Override
    public String getsCode(String sName) {
        return getSettingsFor(sName).getsCode();
    }

    private Settings getSettingsFor(String name) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");
        List<Settings> settings = query.list();

        settings = settings.stream()
                .filter(x -> x.getsName().equals(name) )
                .limit(1)
                .collect(Collectors.toList());


        session.getTransaction().commit();
        HibernateUtil.shutdown();
        return  settings.get(0);
    }

}
