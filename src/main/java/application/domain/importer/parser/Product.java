package application.domain.importer.parser;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {

    private String name;

    private String origin;

    public String measurementUnit;

    private String number;

    private BigDecimal quantity;

    private BigDecimal discount;

    private int tax;

    private BigDecimal price;

    private Timestamp pDate;

    public Timestamp getpDate() {
        return pDate;
    }
    public Product(){};
    public Product(String name, String origin, String measurementUnit,
                   String number, BigDecimal quantity, BigDecimal discount,
                   int tax, BigDecimal price, Timestamp pDate) {
        this.name = name;
        this.origin = origin;
        this.measurementUnit = measurementUnit;
        this.number = number;
        this.quantity = quantity;
        this.discount = discount;
        this.tax = tax;
        this.price = price;
        this.pDate = pDate;
    }
    public void setpDate(Timestamp pDate) {
        this.pDate = pDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
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
        this.quantity = new BigDecimal( quantity);
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

    public void setTax(String tax) {
        this.tax = Integer.parseInt(tax);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }
}