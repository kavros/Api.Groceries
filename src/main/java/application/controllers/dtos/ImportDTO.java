package application.controllers.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ImportDTO {
    public class Entry {
        public String name;
        public String origin;
        public float invoicePrice;
        public float retailPrice;
        public BigDecimal newPrice;
        public float profitInEuro;
        public List<Float> records;
        public String number;
        public String sCode;

        public Entry() { }

        @Override
        public String toString() {
            return "Row{" +
                    "name='" + name + '\'' +
                    ", invoicePrice=" + invoicePrice +
                    ", retailPrice=" + retailPrice +
                    ", newPrice=" + newPrice +
                    ", profitInEuro=" + profitInEuro +
                    ", records=" + records +
                    '}';
        }
    }

    public enum ErrorCode {
        FAILED_TO_PARSE_DATE,
        FAILED_TO_RETRIEVE_SETTING
    }

    public static class Error {
        public String msg;
        public ErrorCode code;
    }

    public ArrayList<Error> errors    = new ArrayList<>();
    public List<Entry> data = new ArrayList<>();
    public String invoiceDate;
}
