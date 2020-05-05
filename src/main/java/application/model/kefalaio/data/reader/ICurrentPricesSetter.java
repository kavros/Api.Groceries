package application.model.kefalaio.data.reader;

import application.model.invoice.Product;

import java.util.List;

public interface ICurrentPricesSetter {
    public void setCurrentPrices(List<Product> products);
}
