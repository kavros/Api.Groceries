package application.model.smast;



import java.util.LinkedList;
import java.util.List;

public class RetailPrices {
    public class RetailProductPrice {
        public String sCode;
        public double currentPrice;
    }

    public List<RetailProductPrice> currentPricesList;

    public RetailPrices(List<Smast> data) {
        currentPricesList = new LinkedList<>();
        data.forEach( item -> {
            RetailProductPrice x = new RetailProductPrice();
            x.currentPrice = item.getsRetailPr();
            x.sCode = item.getsCode();
            currentPricesList.add(x);
        });
    }
}
