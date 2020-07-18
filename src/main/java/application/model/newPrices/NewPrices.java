package application.model.newPrices;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "Api_Groceries.dbo.NewPrices")
public class NewPrices {

    private String sName;
    private String sCode;
    private Timestamp pDate;
    private  float newPrice;

}
