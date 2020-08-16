package application.model.settings;

import javax.persistence.*;

@Entity
@Table(name = "Api_Groceries.dbo.Settings")
public class Settings {

    @Id
    @Column(name = "sName", unique = true, nullable = false, length = 255)
    private String sName;

    @Column(name = "sCode", unique = true, nullable = false, length = 255)
    private String sCode;

    @Column(name = "profit", unique = false, nullable = true)
    private Float profitPercentage;

    @Column(name = "minProfit", unique = false, nullable = true)
    private Float minProfit;

    public String getsCode() {
        return sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public Float getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(Float profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public Float getMinProfit() {
        return minProfit;
    }

    public void setMinProfit(Float minProfit) {
        this.minProfit = minProfit;
    }

    public void setsCode(String sCode) {

        this.sCode = sCode;
    }

}
