package application.model.records;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "Api_Groceries.dbo.Record")
public class Record {


    @Column(name = "invoicePrice", nullable = false)
    private Float invoicePrice;

    @Id
    @Column(name = "productName", nullable = false, unique = true, length = 150)
    private String productName;

    @Id
    @Column(name = "invoiceDate", nullable = false, unique = true )
    private Date invoiceDate;

    public Float getInvoicePrice() {
        return invoicePrice;
    }

    public String getProductName() {
        return productName;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setInvoicePrice(Float invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
