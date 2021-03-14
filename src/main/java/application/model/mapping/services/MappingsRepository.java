package application.model.mapping.services;

import application.hibernate.IHibernateUtil;
import application.model.mapping.Mapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("mappingsRepository")
public class MappingsRepository implements IMappingsRepository {
    @Autowired
    IHibernateUtil dbConnection;

    @Override
    public List getMappings() {
        return dbConnection.getElements(Mapping.class.getName());
    }

    public void deleteMapping(Mapping mapping) {
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.delete(mapping);
        tx.commit();
        session.close();
    }

    public void saveMapping(Mapping mapping) {
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(mapping);
        tx.commit();
        session.close();
    }

    public void updateMapping(Mapping mapping) {
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.update(mapping);
        tx.commit();
        session.close();
    }
}
