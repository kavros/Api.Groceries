package application.domain.table;


import java.util.List;


public class Row {
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




