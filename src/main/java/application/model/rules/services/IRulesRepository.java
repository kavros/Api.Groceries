package application.model.rules.services;

import application.model.rules.Rules;

import java.util.List;

public interface IRulesRepository {
    List<Rules> getRules();
}
