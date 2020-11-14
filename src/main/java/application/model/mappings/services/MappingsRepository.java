package application.model.mappings.services;

import application.hibernate.HibernateUtil;
import application.model.mappings.Mappings;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("mappingsRepository")
public class MappingsRepository implements IMappingsRepository {
    @Override
    public List getMappings() {
        return HibernateUtil.getElements("Mappings");
    }
    public void saveMapping(Mappings mapping) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(mapping);
        tx.commit();
        session.close();
    }
}
