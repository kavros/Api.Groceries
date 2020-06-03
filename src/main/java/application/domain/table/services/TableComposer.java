package application.domain.table.services;

import application.domain.table.Row;
import application.domain.table.TableComposerDTO;
import application.model.records.services.IRecordRepository;
import application.model.invoice.services.IInvoiceParser;
import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
import application.model.invoice.InvoiceProduct;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
        } catch(IllegalArgumentException ex) { // failed to parse line inside the file
            //ex.printStackTrace();
            response.warnings.add(ex.getMessage());
        }catch (ParseException ex) { // failed to retrieve date and time from invoice
            //ex.printStackTrace();
            response.errors.add(ex.getMessage());
        }


        StoreInvoicePrices(response);
        Map<String,Settings> settingsMap = settingsRepo.getSettings();
        Map<String, Smast> sCodeToRetailPrice = retailPricesRepo.getRetailPrices();
        List<String> productNames = invoiceParser.getProducts().stream().map(x->x.name).collect(Collectors.toList());
        Map<String, List<Float>> latestPrices = recordRepo.getLatestPrices(productNames);

        try {

            invoiceParser.getProducts().forEach(x -> {
                Row row = new Row();
                String sCode = settingsMap.get(x.name).getsCode();
                row.name = x.name;
                row.invoicePrice = x.invoicePrice;
                row.profitPercentage = settingsMap.get(x.name).getProfit();
                row.profitInEuro = settingsMap.get(x.name).getMinProfit();
                row.retailPrice = sCodeToRetailPrice.get(sCode).getsRetailPr();
                row.newPrice = (row.invoicePrice * 1.13) * (row.profitPercentage + 1);
                row.records = latestPrices.get(x.name);
                //System.out.println(row);
                response.data.add(row);

            });

        } catch (NoSuchElementException ex) { // failed to retrieve setting for a product
            response.errors.add(ex.getMessage());
        }
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
        } catch (HibernateException ex ){ // file has been imported
            response.warnings.add(ex.getMessage());
        }
    }

}
