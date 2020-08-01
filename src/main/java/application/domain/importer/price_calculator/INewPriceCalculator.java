package application.domain.importer.price_calculator;

public interface INewPriceCalculator {

    float getNewPrice(String sName, float invoicePrice) ;
}
