package application.model.settings.services;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("settingsRepository")
public class SettingsRepository implements ISettingsRepository {


    @Override
    public Map<String,Settings> getAllSettings() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");

        Map<String, Settings> settings = ((List<Settings>)query.list())
                .stream()
                .collect(Collectors.toMap(x -> x.getsName(), x-> x));

        session.getTransaction().commit();


        return  settings;
    }



}
