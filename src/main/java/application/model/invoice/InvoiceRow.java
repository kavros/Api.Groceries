package application.model.invoice;

public class InvoiceRow {
    public String name;
    public String origin;
    public String measurement_unit;
    public String number;

    public double quantity; // ?????
    public double discount;
    public int tax;

    public String kefalaioCode;
    public double invoicePrice;

    public InvoiceRow()
    {

    }
    @Override
    public String toString() {
        return "InvoiceRow{" +
                "name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", tax=" + tax +
                ", kefalaioCode='" + kefalaioCode +
                '}';
    }
}
