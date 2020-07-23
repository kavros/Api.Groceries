package application.controllers.dtos;


import java.util.List;


public class UploadEntry {
    public String name;
    public float profitPercentage;
    public float invoicePrice;
    public float retailPrice;
    public float newPrice;
    public float profitInEuro;
    public List<Float> records;

    @Override
    public String toString() {
        return "Row{" +
                "name='" + name + '\'' +
                ", profitPercentage=" + profitPercentage +
                ", invoicePrice=" + invoicePrice +
                ", retailPrice=" + retailPrice +
                ", newPrice=" + newPrice +
                ", profitInEuro=" + profitInEuro +
                ", records=" + records +
                '}';
    }
}




