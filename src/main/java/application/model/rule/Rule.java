package application.model.rule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "Api_Groceries.dbo.Rules")
public class Rule {

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

    public Rule() {}
    public Rule(String code, float profitPerf, float minProf) {
        this.sCode = code;
        this.profitPercentage = BigDecimal.valueOf(profitPerf);
        this.minProfit = BigDecimal.valueOf(minProf);
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

    public boolean isValid() {
        boolean isMinProfitValid = this.getMinProfit() > 0;
        boolean isPercentageValid = getProfitPercentage() > 0
                && getProfitPercentage() < 1;

        return isMinProfitValid && isPercentageValid;
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
