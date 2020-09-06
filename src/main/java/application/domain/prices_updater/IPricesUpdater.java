package application.domain.prices_updater;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IPricesUpdater {
    void updatePrices(List<Map.Entry<String, BigDecimal>> data, String invoiceDate);
}
