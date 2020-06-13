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
        Map<String, List<Float>> latestPrices = invoiceRepo.getLatestPrices(parserResult.invoiceProducts.stream().map(x->x.id.name).collect(Collectors.toList()));

        try {

            parserResult.invoiceProducts.forEach(x -> {

                Settings setting = getSettings(x.id.name, settingsMap);
                String sCode = setting.getsCode();
                Smast smast = getKefalaioData(sCode, sCodeToRetailPrice);

                Row r = new Row();
                r.name = x.id.name;
                r.invoicePrice = x.price;
                r.profitPercentage = setting.getProfit();
                r.profitInEuro = setting.getMinProfit();
                r.retailPrice = smast.getsRetailPr();

                r.newPrice = getNewPrice(r.profitPercentage, r.invoicePrice);
                r.records = latestPrices.get(x.id.name);

                response.data.add(r);

            });

        } catch (NoSuchElementException ex) { // failed to retrieve setting for a product
            response.errors.add(ex.getMessage());
        }
        return response;
    }

    private Settings getSettings(String sName,  Map<String,Settings> settingsMap){
        Settings setting = settingsMap.get(sName);
        if(setting == null){
            throw new NoSuchElementException("Failed to retrieve sCode for "+sName);
        }
        return setting;
    }

    private Smast getKefalaioData(String sCode, Map<String, Smast> sCodeToRetailPrice ) {
        Smast smast = sCodeToRetailPrice.get(sCode);
        if(smast ==  null){
            throw new NoSuchElementException("Failed to retrieve retail price for "+sCode);
        }
        return smast;
    }

    private float getNewPrice(float profitPercentage, float invoicePrice){
        float priceWithTax = (float) (invoicePrice * 1.13);

        return  priceWithTax * (profitPercentage + 1);
    }
}
