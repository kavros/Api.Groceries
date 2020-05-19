package application.domain.settings.repo;

import application.hibernate.HibernateUtil;
import application.model.settings.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component("settingsRepository")
public class SettingsRepository implements ISettingsRepository {

    private HashMap<String, Settings> settings2;

    public SettingsRepository() {
        loadSettings();
    }

    private void loadSettings() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Settings");
        List<Settings> settings = query.list();

        settings2 = new HashMap<>();
        settings.forEach(item ->
            settings2.put(item.getsName(), item)
        );

        session.getTransaction().commit();
        HibernateUtil.shutdown();
    }

    @Override
    public Float getProfit(String sName) {
        return settings2.get(sName).getProfit();
    }

    @Override
    public Float getMinProfit(String sName) {
        return settings2.get(sName).getMinProfit();
    }

    @Override
    public String getsCode(String sName) {
        return settings2.get(sName).getsCode();
    }


}
