package application.model.rules.services;

import application.model.rules.Rules;

import java.util.List;

public interface IRulesRepository {
    List<String> getScodes();
    List<Rules> getRules();
}
