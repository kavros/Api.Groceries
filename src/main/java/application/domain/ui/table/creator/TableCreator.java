package application.domain.ui.table.creator;

import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.repo.ISettingsRepository;
import application.model.invoice.InvoiceProduct;
import application.domain.current.prices.ICurrentPricesRepository;
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
    ICurrentPricesRepository currentPricesRepo;

    @Override
    public Table createTable(String invoiceContent) {

        //load invoice data
        invoiceParser.parseInvoice(invoiceContent);
        List<String> sCodes = getSCodes( invoiceParser.getProducts());

        //load current prices
        currentPricesRepo.loadCurrentPrices(sCodes);

        Table table =  new Table( invoiceParser, settingsRepo, currentPricesRepo);


        calculateAndSetNewPrices(table);
        setCheckboxAndIcons(table);


        return table;
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
