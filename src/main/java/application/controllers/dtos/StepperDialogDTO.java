package application.controllers.dtos;

public class StepperDialogDTO {
    String sName;
    float profitPercentage;
    float minProfit;
    String sCode;
    String pName;

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
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

}
