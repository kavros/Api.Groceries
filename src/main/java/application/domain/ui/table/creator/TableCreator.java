package application.domain.ui.table.creator;

import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.parser.ISettingsParser;
import application.model.invoice.Invoice;
import application.model.kefalaio.data.reader.CurrentProductPrices;
import application.model.kefalaio.data.reader.ICurrentPricesRepository;
import application.model.settings.Settings;
import application.model.table.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component("tableCreator")
public class TableCreator implements ITableCreator {

    @Autowired
    ISettingsParser settingsParser;
    @Autowired
    IInvoiceParser invoiceParser;
    @Autowired
    ICurrentPricesRepository currentPricesLoader;

    @Override
    public Table createTable(String invoiceContent) {

        Settings settings = settingsParser.getSettings();
        Invoice invoice = invoiceParser.getInvoice(invoiceContent);
        List<String> sCodes = getSCodesToRequest(settings, invoice);
        CurrentProductPrices currentPrices = currentPricesLoader.getCurrentPrices(sCodes);

        Table table =  new Table(invoice, settings, currentPrices);

        calculateAndSetNewPrices(table);
        setCheckboxAndIcons(table);


        return table;
    }

    private List<String> getSCodesToRequest(Settings settings, Invoice invoice ) {
        List<String> sCodes = new ArrayList<>();


        /*invoice.invoiceRows.forEach(i -> settings.settingsRows.forEach(r -> {
            if(r.name.equals(i.name)){
                sCodes.add(r.sCode);
            }
        }));*/

        return sCodes;
    }

    private void calculateAndSetNewPrices(Table table){

    }

    private void setCheckboxAndIcons(Table table){

    }


}
