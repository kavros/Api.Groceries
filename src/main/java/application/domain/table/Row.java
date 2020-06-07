package application.domain.table;


import java.util.List;


public class Row {
    public String name;
    public double profitPercentage;
    public double invoicePrice;
    public double retailPrice;
    public double newPrice;
    public double profitInEuro;
    public List<Double> records;

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




