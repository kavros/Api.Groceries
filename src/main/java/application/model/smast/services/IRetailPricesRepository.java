package application.model.smast.services;

import application.model.smast.RetailPrices;

import java.util.List;

public interface IRetailPricesRepository {
    public RetailPrices loadRetailPrices(List<String> sCodes);
    public Float getRetailPrice(String sCode);

}
