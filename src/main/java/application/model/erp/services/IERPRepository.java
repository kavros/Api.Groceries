package application.model.erp.services;

import application.model.erp.Smast;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IERPRepository {
    List<Smast> getProducts(List<String> sCodes);
    String getSName(String sCode);
    void updatePrices(List<Map.Entry<String, BigDecimal>> sCodesAndPrices);
}
