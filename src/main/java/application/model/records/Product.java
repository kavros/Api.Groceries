package application.model.records;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="Api_Groceries.dbo.Invoices")
public class Product {

    @EmbeddedId
    private ProductId id = new ProductId();

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

    @Column(name = "newPrice", nullable = false )
    public float newPrice;

    public float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        this.newPrice = newPrice;
    }

    public Timestamp getpDate() {
        return id.getpDate();
    }

    public void setpDate(Timestamp pDate) {
        id.setpDate(pDate);
    }

    public String getName() {
        return id.getName();
    }

    public void setName(String name) {
        id.setName(name);
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMeasurement_unit() {
        return measurement_unit;
    }

    public void setMeasurement_unit(String measurement_unit) {
        this.measurement_unit = measurement_unit;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "InvoiceProduct{" +
                "name='" + id.getName() + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", tax=" + tax +
                ", pdate=" + id.getpDate() +
                '}';
    }


}
