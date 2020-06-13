package application.model.invoice;

import javax.persistence.*;

@Entity
@Table(name="Api_Groceries.dbo.Invoices")
public class InvoiceProduct {

    @EmbeddedId
    public InvoiceProductId id = new InvoiceProductId();

    @Column(name = "origin", nullable = false )
    public String origin;

    @Column(name = "measurement_unit", nullable = false )
    public String measurement_unit;

    @Column(name = "number", nullable = false )
    public String number;

    @Column(name = "quantity", nullable = false )
    public double quantity;

    @Column(name = "discount", nullable = false )
    public double discount;

    @Column(name = "tax", nullable = false )
    public int tax;

    @Column(name = "price", nullable = false )
    public float price;


    @Override
    public String toString() {
        return "InvoiceProduct{" +
                "name='" + id.name + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", tax=" + tax +
                ", pdate=" + id.pDate +
                '}';
    }


}
