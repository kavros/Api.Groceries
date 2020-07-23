package application.domain.upload.services;

public interface INewPriceCalculator {

    float getNewPrice(String sName, float invoicePrice) ;
}
