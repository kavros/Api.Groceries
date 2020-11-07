package application.model.rules.services;

import application.hibernate.HibernateUtil;
import application.model.rules.Rules;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("rulesRepository")
public class RulesRepository implements IRulesRepository {

    public List<Rules> getRules() {
        return HibernateUtil.getElements("Rules");
    }
}
