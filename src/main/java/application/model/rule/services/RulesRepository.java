package application.model.rule.services;

import application.hibernate.IHibernateUtil;
import application.model.rule.Rule;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("rulesRepository")
public class RulesRepository implements IRulesRepository {
    @Autowired
    IHibernateUtil dbConnection;

    public List<String> getScodes() {
        return getRules()
                .stream()
                .map(Rule::getsCode)
                .collect(Collectors.toList());
    }
    public List<Rule> getRules() {
        return dbConnection.getElements(Rule.class.getName());
    }

    public Rule getRule(String sCode) {
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        String queryText = "from "+ Rule.class.getName() + " rule " +
                "where rule.sCode = :sCode";

        Query query = session.createQuery(queryText);
        query.setParameter("sCode", sCode);
        Rule rule = null;
        if( ! query.getResultList().isEmpty()) {
            rule = (Rule) query.getResultList().get(0);
        }
        session.getTransaction().commit();
        session.close();
        return rule;
    }

    public boolean addOrUpdateRule(Rule newRule) {
        if(! newRule.isValid()) {
            return false;
        }
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(newRule);
        tx.commit();
        session.close();
        return true;
    }

    public void deleteRule(Rule rule) {
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.delete(rule);
        tx.commit();
        session.close();
    }
}
