package application.hibernate;

import org.hibernate.SessionFactory;

import java.util.List;

public interface IHibernateUtil {
    public SessionFactory getSessionFactory();
    public void shutdown();
    public List getElements(String tableName);
}
