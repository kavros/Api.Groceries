package application.domain.table.services;

import application.domain.table.Row;
import application.domain.table.TableComposerDTO;
import application.model.records.services.IRecordRepository;
import application.model.invoice.services.IInvoiceParser;
import application.model.settings.services.ISettingsRepository;
import application.model.invoice.InvoiceProduct;
import application.model.smast.services.IRetailPricesRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component("tableCreator")
public class TableComposer implements ITableComposer {

    @Autowired
    ISettingsRepository settingsRepo;
    @Autowired
    IInvoiceParser invoiceParser;
    @Autowired
    IRetailPricesRepository retailPricesRepo;
    @Autowired
    IRecordRepository recordRepo;



    @Override
    public TableComposerDTO createTable(String invoiceContent) {

        TableComposerDTO response = new TableComposerDTO();

        try {
            invoiceParser.parseInvoice(invoiceContent);
        } catch(IllegalArgumentException ex) {
            //ex.printStackTrace();
            response.warnings.add(ex.getMessage());
        }catch (ParseException ex) {
            //ex.printStackTrace();
            response.errors.add(ex.getMessage());
        }


        StoreInvoicePrices(response);



        invoiceParser.getProducts().forEach( x -> {
                    Row row = new Row();
                    row.name = x.name;
                    row.invoicePrice = x.invoicePrice;
                    row.profitPercentage = settingsRepo.getProfit(x.name);
                    row.profitInEuro = settingsRepo.getMinProfit(x.name);
                    row.retailPrice = retailPricesRepo.getRetailPrice(settingsRepo.getsCode(x.name));
                    row.newPrice = (row.invoicePrice * 1.13) * (row.profitPercentage + 1);
                    row.records = recordRepo.getLatestPriceRecordFor(x.name);
                    //System.out.println(row);
                    response.data.add(row);

        });

        return response;
    }

    private void StoreInvoicePrices(TableComposerDTO response) {
        java.sql.Timestamp dateTime = new java.sql.Timestamp(invoiceParser.getDate().getTime());

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float> prices  = new ArrayList<>();
        for ( InvoiceProduct p : invoiceParser.getProducts()) {
            names.add(p.name);
            prices.add((float)p.invoicePrice);
        }

        try {
            recordRepo.Store(prices, names, dateTime);
        } catch (HibernateException ex ){
            response.warnings.add(ex.getMessage());
        }
    }

}
