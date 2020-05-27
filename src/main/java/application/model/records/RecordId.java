package application.model.records;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RecordId  implements Serializable {

    @Column(name = "invoicePrice", nullable = false)
    private Float invoicePrice;

    @Column(name = "productName", nullable = false, length = 150)
    private String productName;

    public Float getInvoicePrice() {
        return invoicePrice;
    }

    public void setInvoicePrice(Float invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordId)) return false;
        RecordId that = (RecordId) o;
        return getProductName().equals(that.getProductName()) &&
                getInvoicePrice()== that.getInvoicePrice();
    }
}
