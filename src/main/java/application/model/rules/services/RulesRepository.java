package application.model.rules.services;

import application.hibernate.HibernateUtil;
import application.model.rules.Rules;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("rulesRepository")
public class RulesRepository implements IRulesRepository {
    public List<String> getScodes() {
        return getRules()
                .stream()
                .map(Rules::getsCode)
                .collect(Collectors.toList());
    }
    public List<Rules> getRules() {
        return HibernateUtil.getElements("Rules");
    }

    public void saveRule(Rules newRule) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(newRule);
        tx.commit();
        session.close();
    }
}