package application.model.settings.services;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component("settingsRepository")
public class SettingsRepository implements ISettingsRepository {



    @Override
    public Float getProfit(String sName) throws NoSuchElementException {
        return  getSettingsFor(sName).getProfit();
    }

    @Override
    public Float getMinProfit(String sName) throws NoSuchElementException  {
        return getSettingsFor(sName).getMinProfit();
    }

    @Override
    public String getsCode(String sName) throws NoSuchElementException  {
        return getSettingsFor(sName).getsCode();
    }

    private Settings getSettingsFor(String name) throws NoSuchElementException  {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");

        List<Settings> settings = ((List<Settings>)query.list())
                .stream()
                .filter(x -> x.getsName().equals(name) )
                .limit(1)
                .collect(Collectors.toList());


        session.getTransaction().commit();
        HibernateUtil.shutdown();
        if(settings.isEmpty())
        {
            throw new NoSuchElementException ("Failed to retrieve setting for: "+name);
        }
        Settings setting = settings.get(0);

        return  setting;
    }

}
