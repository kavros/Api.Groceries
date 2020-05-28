package application.domain.ui.table.creator;

import application.model.records.services.IRecordRepository;
import application.model.invoice.services.IInvoiceParser;
import application.model.settings.services.ISettingsRepository;
import application.model.invoice.InvoiceProduct;
import application.model.smast.services.IRetailPricesRepository;
import application.domain.table.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component("tableCreator")
public class TableCreator implements ITableCreator {

    @Autowired
    ISettingsRepository settingsRepo;
    @Autowired
    IInvoiceParser invoiceParser;
    @Autowired
    IRetailPricesRepository retailPricesRepo;
    @Autowired
    IRecordRepository recordRepo;


    @Override
    public Table createTable(String invoiceContent) {

        invoiceParser.parseInvoice(invoiceContent);
        List<String> sCodes = getSCodes( invoiceParser.getProducts());

        retailPricesRepo.loadRetailPrices(sCodes);

        StoreInvoicePrices();
        recordRepo.getLastThreeInvoicePricesFor("ΚΟΛΟΚΥΘΙΑ");
        Table table =  new Table( invoiceParser, settingsRepo, retailPricesRepo);


        calculateAndSetNewPrices(table);
        setCheckboxAndIcons(table);


        return table;
    }

    private void StoreInvoicePrices() {
        java.sql.Timestamp dateTime = new java.sql.Timestamp(invoiceParser.getDate().getTime());

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float> prices  = new ArrayList<>();
        for ( InvoiceProduct p : invoiceParser.getProducts()) {
            names.add(p.name);
            prices.add((float)p.invoicePrice);
        }

        recordRepo.Store(prices, names, dateTime);
    }

    private List<String> getSCodes( ArrayList<InvoiceProduct> products) {
        List<String> sCodes = new ArrayList<>();

        for (InvoiceProduct product : products ){
            String sCode = settingsRepo.getsCode(product.name);
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
