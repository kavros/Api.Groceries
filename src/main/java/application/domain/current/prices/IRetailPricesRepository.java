package application.domain.current.prices;

import application.model.smast.RetailPrices;

import java.util.List;

public interface IRetailPricesRepository {
    public RetailPrices loadRetailPrices(List<String> sCodes);
    public Float getRetailPrice(String sCode);

}
