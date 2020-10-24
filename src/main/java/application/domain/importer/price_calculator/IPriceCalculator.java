package application.domain.importer.price_calculator;

import application.model.settings.Settings;

import java.util.List;

public interface IPriceCalculator {
    float getHistoryCatalogPrice(String sCode, float invoicePrice, List<Settings> settings );
    float getNewPrice(String sName, float invoicePrice) ;
}
