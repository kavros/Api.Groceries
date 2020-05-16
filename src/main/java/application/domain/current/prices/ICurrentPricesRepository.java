package application.domain.current.prices;

import application.model.smast.CurrentProductPrices;

import java.util.List;

public interface ICurrentPricesRepository {
    public CurrentProductPrices loadCurrentPrices(List<String> kefalaioCode);
    public Float getCurrentPrice(String sCode);

}
