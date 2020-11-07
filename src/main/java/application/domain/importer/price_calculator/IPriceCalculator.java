package application.domain.importer.price_calculator;

import application.model.rules.Rules;
import java.util.List;

public interface IPriceCalculator {
    float getHistoryCatalogPrice(String sCode, float invoicePrice, List<Rules> settings );
    float getNewPrice(String sName, float invoicePrice) ;
}
