package application.model.kefalaio.data.reader;

import application.model.invoice.InvoiceRow;

import java.util.List;

public interface ICurrentPricesRepository {
    public CurrentProductPrices getCurrentPrices(List<String> kefalaioCode);
}
