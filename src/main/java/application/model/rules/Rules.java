package application.model.rules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "Api_Groceries.dbo.Rules")
public class Rules {

    @Id
    @Column(name = "sCode", unique = true, nullable = false, length = 255)
    private String sCode;

    @Column(name = "profitPercentage", unique = false, nullable = true)
    private BigDecimal profitPercentage;

    @Column(name = "minProfit", unique = false, nullable = true)
    private BigDecimal minProfit;

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public float getProfitPercentage() {
        return profitPercentage.floatValue();
    }

    public void setProfitPercentage(BigDecimal profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public float getMinProfit() {
        return minProfit.floatValue();
    }

    public void setMinProfit(BigDecimal minProfit) {
        this.minProfit = minProfit;
    }

    @Override
    public String toString() {
        return "Rules{" +
                "sCode='" + sCode + '\'' +
                ", profitPercentage=" + profitPercentage +
                ", minProfit=" + minProfit +
                '}';
    }
}
