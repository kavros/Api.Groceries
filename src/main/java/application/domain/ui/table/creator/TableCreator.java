package application.domain.ui.table.creator;

import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.parser.ISettingsParser;
import application.model.invoice.Invoice;
import application.model.invoice.InvoiceRow;
import application.model.smast.CurrentProductPrices;
import application.domain.current.prices.ICurrentPricesRepository;
import application.model.settings.Settings;
import application.domain.table.Table;
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
    ICurrentPricesRepository currentPricesRepo;

    @Override
    public Table createTable(String invoiceContent) {

        //load invoice data
        invoiceParser.parseInvoice(invoiceContent);
        List<String> sCodes = getSCodes( invoiceParser.getProducts());

        //load current prices
        currentPricesRepo.loadCurrentPrices(sCodes);

        Table table =  new Table( invoiceParser, settingsParser, currentPricesRepo);


        calculateAndSetNewPrices(table);
        setCheckboxAndIcons(table);


        return table;
    }

    private List<String> getSCodes( ArrayList<InvoiceRow> products) {
        List<String> sCodes = new ArrayList<>();

        for (InvoiceRow row : products ){
            String sCode = settingsParser.getsCode(row.name);
            if( sCode != null)
            {
                sCodes.add(sCode);
            }else {
                //TODO: handle case where name is not in settings
            }
        }
        return sCodes;
    }

    private void calculateAndSetNewPrices(Table table){

    }

    private void setCheckboxAndIcons(Table table){

    }


}
