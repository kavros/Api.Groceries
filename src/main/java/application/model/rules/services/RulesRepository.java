package application.model.rules.services;

import application.hibernate.HibernateUtil;
import application.model.rules.Rules;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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

    public Rules getRule(String sCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String queryText = "from Rules rule " +
                "where rule.sCode = :sCode";

        Query query = session.createQuery(queryText);
        query.setParameter("sCode", sCode);
        Rules rule = null;
        if( ! query.getResultList().isEmpty()) {
            rule = (Rules) query.getResultList().get(0);
        }
        session.getTransaction().commit();
        session.close();
        return rule;
    }

    public void addOrUpdateRule(Rules newRule) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(newRule);
        tx.commit();
        session.close();
    }

    public void deleteRule(Rules rule) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.delete(rule);
        tx.commit();
        session.close();
    }
}
