package application.model.kefalaio.data.reader;

import java.util.List;

public class CurrentProductPrices {
    class CurrentProductPrice {
        String kefalaioCode;
        double currentPrice;
    }

    List<CurrentProductPrice> currentPricesList;
}
