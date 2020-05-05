package application.model.invoice;

public class Product {
    public String name;
    public String origin;
    public String measurement_unit;
    public String number;

    public double quantity; // ?????
    public double discount;
    public int tax;

    public String kefalaioCode;
    public ProductPrices prices;

    public Product()
    {
        prices = new ProductPrices();
    }
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", tax=" + tax +
                ", kefalaioCode='" + kefalaioCode + '\'' +
                ", prices=" + prices.toString() +
                '}';
    }
}
