package application.model.smast.services;

import application.model.smast.Smast;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IRetailPricesRepository {
    List<Smast> getRetailPrices(List<String> sCodes);
    String getSName(String sCode);
    void updatePrices(List<Map.Entry<String, BigDecimal>> sCodesAndPrices);
}
