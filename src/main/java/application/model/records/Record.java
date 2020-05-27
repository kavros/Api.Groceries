package application.model.records;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Api_Groceries.dbo.Record")
public class Record {


    @EmbeddedId
    private RecordId id = new RecordId();

    @Column(name = "invoiceDate", nullable = false )
    private Timestamp invoiceDate;

    public Float getInvoicePrice() {
        return this.id.getInvoicePrice();
    }

    public String getProductName() {
        return this.id.getProductName();
    }

    public Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Timestamp invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setInvoicePrice(Float invoicePrice) {
        this.id.setInvoicePrice( invoicePrice);
    }

    public void setProductName(String productName) {
        this.id.setProductName( productName);
    }

}
