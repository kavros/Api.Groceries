package application.controllers.dtos;

public class RulesTableRowDTO implements Comparable<RulesTableRowDTO> {
    private String sCode;
    private float profitPercentage;
    private float minProfit;
    private String sName;

    public RulesTableRowDTO(String sCode, float profitPercentage, float minProfit, String sName) {
        this.sCode = sCode;
        this.profitPercentage = profitPercentage;
        this.minProfit = minProfit;
        this.sName = sName;
    }

    public RulesTableRowDTO(String sCode, String sName) {
        this.sCode = sCode;
        this.sName = sName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public float getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(float profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public float getMinProfit() {
        return minProfit;
    }

    public void setMinProfit(float minProfit) {
        this.minProfit = minProfit;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    @Override
    public int compareTo(RulesTableRowDTO o) {
        return this.getsName().compareTo(o.getsName());
    }
}
