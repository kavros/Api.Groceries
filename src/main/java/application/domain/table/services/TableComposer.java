package application.domain.table.services;

import application.domain.table.Row;
import application.domain.table.TableComposerDTO;
import application.model.invoice.services.ParserResult;

import application.model.invoice.services.IInvoiceRepository;
import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component("tableCreator")
public class TableComposer implements ITableComposer {

    @Autowired
    ISettingsRepository settingsRepo;
    @Autowired
    IInvoiceRepository invoiceRepo;
    @Autowired
    IRetailPricesRepository retailPricesRepo;



    @Override
    public TableComposerDTO createTable(String invoiceContent) {

        TableComposerDTO response = new TableComposerDTO();
        ParserResult parserResult = null;
        try {
            parserResult = invoiceRepo.parseInvoice(invoiceContent);
        } catch (ParseException ex) { // failed to retrieve date and time from invoice
            response.errors.add(ex.getMessage());
        }

        response.warnings.addAll(parserResult.warnings);
        response.invoiceDate = parserResult.invoiceDate;
        Map<String,Settings> settingsMap = settingsRepo.getSettings();
        List<String> sCodes = settingsMap
                .entrySet().stream()
                .map(x -> x.getValue().getsCode())
                .collect(Collectors.toList());

        Map<String, Smast> sCodeToRetailPrice = retailPricesRepo.getRetailPrices(sCodes);
        Map<String, List<Double>> latestPrices = invoiceRepo.getLatestPrices(parserResult.invoiceProducts.stream().map(x->x.id.name).collect(Collectors.toList()));

        try {

            parserResult.invoiceProducts.forEach(x -> {
                Row row = new Row();
                String sCode = settingsMap.get(x.id.name).getsCode();
                row.name = x.id.name;
                row.invoicePrice = x.price;
                row.profitPercentage = settingsMap.get(x.id.name).getProfit();
                row.profitInEuro = settingsMap.get(x.id.name).getMinProfit();
                row.retailPrice = sCodeToRetailPrice.get(sCode).getsRetailPr();
                row.newPrice = (row.invoicePrice * 1.13) * (row.profitPercentage + 1);
                row.records = latestPrices.get(x.id.name);
                //System.out.println(row);
                response.data.add(row);

            });

        } catch (NoSuchElementException ex) { // failed to retrieve setting for a product
            response.errors.add(ex.getMessage());
        }
        return response;
    }

}
