package application.model.settings.services;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component("settingsRepository")
public class SettingsRepository implements ISettingsRepository {

    public Map<String,Settings> getSnameToSettingMap() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");

        Map<String, Settings> settings = ((List<Settings>)query.list())
                .stream()
                .collect(Collectors.toMap(x -> x.getsName(), x-> x));

        session.getTransaction().commit();

        return  settings;
    }

    public List<Settings> getSettings() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");

        return query.list();
    }

    public void add(Settings setting) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(setting);
        tx.commit();
    }
}
