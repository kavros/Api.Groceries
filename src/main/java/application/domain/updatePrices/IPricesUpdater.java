package application.domain.updatePrices;

import java.util.List;
import java.util.Map;

public interface IPricesUpdater {
    void updatePrices(List<Map.Entry<String,Float>> data, String invoiceDate);
}
