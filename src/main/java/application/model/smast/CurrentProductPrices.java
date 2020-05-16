package application.model.smast;



import java.util.LinkedList;
import java.util.List;

public class CurrentProductPrices {
    public class CurrentProductPrice {
        public String kefalaioCode;
        public double currentPrice;
    }

    public List<CurrentProductPrice> currentPricesList;

    public CurrentProductPrices( List<Smast> data) {
        currentPricesList = new LinkedList<>();
        data.forEach( item -> {
            CurrentProductPrice x = new CurrentProductPrice();
            x.currentPrice = item.getsRetailPr();
            x.kefalaioCode = item.getsCode();
            currentPricesList.add(x);
        });
    }
}
