package application.model.invoice;

public class InvoiceProduct {
    public String name;
    public String origin;
    public String measurement_unit;
    public String number;

    public double quantity;
    public double discount;
    public int tax;

    public double invoicePrice;


    @Override
    public String toString() {
        return "InvoiceProduct{" +
                "name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", tax=" + tax +
                '}';
    }
}
