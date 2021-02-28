package application.controllers.settings.dtos;

import java.util.List;

public class RulesTableRowDTO implements Comparable<RulesTableRowDTO> {
    private String sCode;
    private float profitPercentage;
    private float minProfit;
    private String sName;



    private List<String> pNames;
    public RulesTableRowDTO(String sCode,
                            float profitPercentage,
                            float minProfit,
                            String sName,
                            List<String> pNames) {
        this.sCode = sCode;
        this.profitPercentage = profitPercentage;
        this.minProfit = minProfit;
        this.sName = sName;
        this.pNames = pNames;
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

    public List<String> getpNames() {
        return pNames;
    }

    public void setpNames(List<String> pNames) {
        this.pNames = pNames;
    }

    @Override
    public int compareTo(RulesTableRowDTO o) {
        return this.getsName().compareTo(o.getsName());
    }
}
