package application.controllers.dtos;

import java.util.ArrayList;
import java.util.List;

public class UploadDTO {
    public class Entry {
        public String name;
        public float profitPercentage;
        public float invoicePrice;
        public float retailPrice;
        public float newPrice;
        public float profitInEuro;
        public List<Float> records;

        public Entry() {
        }

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


    public ArrayList<String> warnings  = new ArrayList<>();
    public ArrayList<String> errors    = new ArrayList<>();
    public List<Entry> data = new ArrayList<>();
    public String invoiceDate;
}
