package application.domain.importer.price_calculator;

import application.model.mappings.Mappings;
import application.model.rule.Rule;
import java.util.List;

public interface IPriceCalculator {
    float getHistoryCatalogPrice(String sCode, float invoicePrice, List<Rule> settings );
    float getNewPrice(String sName, float invoicePrice, List<Mappings> mappings, List<Rule> rules);
}
