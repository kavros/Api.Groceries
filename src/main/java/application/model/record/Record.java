package application.model.record;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name="Api_Groceries.dbo.Records")
public class Record {

    @EmbeddedId
    private ProductId id = new ProductId();

    @Column(name = "origin", nullable = false )
    public String origin;

    @Column(name = "measurement_unit", nullable = false )
    public String measurement_unit;

    @Column(name = "number", nullable = false )
    public String number;

    @Column(name = "quantity", nullable = false )
    private BigDecimal quantity;

    @Column(name = "discount", nullable = false )
    private BigDecimal discount;

    @Column(name = "tax", nullable = false )
    public int tax;

    @Column(name = "price", nullable = false )
    private BigDecimal price;

    @Column(name = "newPrice", nullable = false )
    private BigDecimal newPrice;

    @Column(name = "sCode", nullable = false)
    public String sCode;

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        String new_price = String.valueOf(newPrice);
        this.newPrice = new BigDecimal( new_price);
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = new BigDecimal(newPrice);
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
        name = name.replace('Ϊ','Ι');
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = new BigDecimal(quantity);
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = new BigDecimal(discount);
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }
    public void setTax(String tax) {
        this.tax = Integer.parseInt((tax));
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }
    @Override
    public String toString() {
        return "{" +
                "name='" + id.getName() + '\'' +
                ", origin='" + origin + '\'' +
                ", measurement_unit='" + measurement_unit + '\'' +
                ", number='" + number + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", discount=" + discount +
                ", tax=" + tax +
                ", pdate=" + id.getpDate() +
                "}\n";
    }


}
