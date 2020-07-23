package application.controllers.dtos;


import java.sql.Timestamp;
import java.util.List;

public class UpdatePricesDTO {
    private List<UpdatePricesEntry> products;
    private String invoiceDate;

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    public List<UpdatePricesEntry> getProducts() {
        return products;
    }

    public void setProducts(List<UpdatePricesEntry> products) {
        this.products = products;
    }
}
