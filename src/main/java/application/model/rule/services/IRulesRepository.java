package application.model.rule.services;

import application.model.rule.Rule;

import java.util.List;

public interface IRulesRepository {
    List<String> getScodes();
    List<Rule> getRules();
    boolean addOrUpdateRule(Rule newRule);
    void deleteRule(Rule newRule);
    Rule getRule(String sCode);
}
