package application.model.record;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

@Embeddable
public class ProductId implements Serializable {


    @Column(name = "name", nullable = false )
    private String name;

    @Column(name = "pDate", nullable = false )
    private Timestamp pDate;

    public Timestamp getpDate() {
        return pDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId)) return false;
        ProductId that = (ProductId) o;
        return name.equals(that.name) &&
                pDate.equals(that.pDate);
    }
}
