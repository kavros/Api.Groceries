package application.model.invoice;


public class ProductPrices {
    public double currentPrice;
    public double invoicePrice;
    public double newPrice;

    @Override
    public String toString() {
        return "ProductPrices{" +
                "currentPrice=" + currentPrice +
                ", invoicePrice=" + invoicePrice +
                ", newPrice=" + newPrice +
                '}';
    }
//    Date date;
}
