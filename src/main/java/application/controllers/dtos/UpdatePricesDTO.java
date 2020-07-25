package application.controllers.dtos;

import java.util.List;

public class UpdatePricesDTO {
    public static class Entry {
        private String name;
        private float newPrice;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getNewPrice() {
            return newPrice;
        }

        public void setNewPrice(float newPrice) {
            this.newPrice = newPrice;
        }
        
        @Override
        public String toString() {
            return "{" +
                    "name='" + name + '\'' +
                    ", newPrice=" + newPrice +
                    '}';
        }
    }


    private List<Entry> products;
    private String invoiceDate;

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    public List<Entry> getProducts() {
        return products;
    }

    public void setProducts(List<Entry> products) {
        this.products = products;
    }
}
