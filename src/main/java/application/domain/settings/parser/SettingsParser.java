package application.domain.settings.parser;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("settingsParser")
public class SettingsParser implements ISettingsParser {



    @Override
    public Settings getSettings() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");
        List<Settings> list = query.list();

        list.forEach(x -> {
            System.out.println(x.getsName());
        });



        session.getTransaction().commit();
        HibernateUtil.shutdown();

        return null;
    }


}
