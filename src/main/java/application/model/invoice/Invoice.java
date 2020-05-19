package application.model.invoice;

import java.util.ArrayList;
import java.util.Date;

public class Invoice {
    public ArrayList<InvoiceProduct> invoiceProducts;
    public Date date;

    public Invoice() {
        invoiceProducts = new ArrayList<>();
    }
}
