package application.domain.importer;

import java.math.BigDecimal;

public class Product {

    private String name;

    private String origin;

    public String measurementUnit;

    private String number;

    private BigDecimal quantity;

    private BigDecimal discount;

    private int tax;

    private BigDecimal price;

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