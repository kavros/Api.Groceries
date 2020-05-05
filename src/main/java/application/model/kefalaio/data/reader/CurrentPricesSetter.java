package application.model.kefalaio.data.reader;

import application.model.invoice.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("currentPricesSetter")
public class CurrentPricesSetter implements ICurrentPricesSetter {

    public void setCurrentPrices(List<Product> products) {

    }
}
