package application.domain.ui.table.creator;

import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.parser.ISettingsParser;
import application.model.invoice.Invoice;
import application.model.invoice.Product;
import application.model.kefalaio.data.reader.ICurrentPricesSetter;
import application.model.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("tableCreator")
public class TableCreator implements ITableCreator {

    @Autowired
    ISettingsParser settingsParser;
    @Autowired
    IInvoiceParser invoiceParser;
    @Autowired
    ICurrentPricesSetter currentPricesSetter;

    @Override
    public Table createTable(String invoiceContent) {
        Table table =  new Table();
        Settings settings = settingsParser.getSettings();
        Invoice invoice = invoiceParser.getInvoice(invoiceContent);
        currentPricesSetter.setCurrentPrices(invoice.products);

        setNewPrices(invoice.products, settings);
        setChangeIcons(invoice.products);

        table.products = invoice.products;
        return table;
    }


    private void setNewPrices(List<Product> products, Settings settings){

    }

    private void setChangeIcons(List<Product> products){

    }


}
