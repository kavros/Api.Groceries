package application.model.history;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "Api_Groceries.dbo.PricesHistory")
public class PricesHistory {


    @Column(name = "retailPrice", nullable = false)
    private Float retailPrice;

    @Column(name = "invoicePrice", nullable = false)
    private Float invoicePrice;

    @Id
    @Column(name = "productName", nullable = false, unique = true, length = 150)
    private String productName;

    @Id
    @Column(name = "invoiceDate", nullable = false, unique = true )
    private Date invoiceDate;


}
