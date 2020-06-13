package application.model.smast.services;

import application.model.smast.Smast;
import java.util.List;
import java.util.Map;

public interface IRetailPricesRepository {

    Map<String,Smast> getRetailPrices(List<String> sCodes);

}
