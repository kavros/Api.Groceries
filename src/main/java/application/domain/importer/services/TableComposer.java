package application.domain.importer.services;

import application.controllers.dtos.ImportDTO;
import application.domain.importer.parser.InvoiceParser;
import application.domain.importer.parser.ParserResult;

import application.model.records.services.IRecordsRepository;
import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    IRecordsRepository recordsRepo;
    @Autowired
    IRetailPricesRepository retailPricesRepo;
    @Autowired
    InvoiceParser parser;

    @Override
    public ImportDTO createTable(String invoiceContent) {

        ImportDTO response = new ImportDTO();
        ParserResult parserResult = null;
        try {
            parserResult = parser.parseAndLoad(invoiceContent);
        } catch (ParseException ex) {
            ImportDTO.Error err  = getError (
                        ImportDTO.ErrorCode.FAILED_TO_PARSE_DATE,
                        ex.getMessage()
                    );
            response.errors.add(err);
            return response;
        }catch (NoSuchElementException ex ) {
            // NoSuchElementException: failed to get Setting for a product name
            ImportDTO.Error err  = getError (
                    ImportDTO.ErrorCode.FAILED_TO_RETRIEVE_SETTING,
                    ex.getMessage()
            );
            response.errors.add(err);
            return response;
        }

        response.warnings.addAll(parserResult.warnings);
        response.invoiceDate =  parserResult.invoiceDate.toString();
        Map<String,Settings> settingsMap = settingsRepo.getAllSettings();
        List<String> sCodes = settingsMap
                .entrySet().stream()
                .map(x -> x.getValue().getsCode())
                .collect(Collectors.toList());

        Map<String, Smast> sCodeToRetailPrice = retailPricesRepo.getRetailPrices(sCodes);
        Map<String, List<Float>> latestPrices = recordsRepo
                .getLatestPrices(
                    parserResult
                        .products
                        .stream()
                        .map(x->x.getName())
                        .collect(Collectors.toList())
                );


        parserResult.products.forEach(x -> {

            Settings setting = getSettings(x.getName(), settingsMap);
            String sCode = setting.getsCode();
            Smast smast = getKefalaioData(sCode, sCodeToRetailPrice);

            ImportDTO.Entry r = response.new Entry();
            r.name = x.getName();
            r.invoicePrice = x.price;
            r.profitPercentage = setting.getProfit();

            r.retailPrice = smast.getsRetailPr();

            r.newPrice = x.newPrice;
            r.profitInEuro = getActualProfit(r.newPrice,r.invoicePrice);
            r.records = latestPrices.get(x.getName());

            response.data.add(r);

        });


        return response;
    }

    private ImportDTO.Error getError(ImportDTO.ErrorCode code, String msg){
        ImportDTO.Error err  = new ImportDTO.Error();
        err.code = code;
        err.msg = msg;
        return err;
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

    private float getActualProfit(float newPrice,float invoicePrice){
        float actualProfit = newPrice- (float)(invoicePrice*1.13);

        return round2Decimals(actualProfit);
    }

    private float round2Decimals(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String roundedNumber = df.format(number).replace(',','.');

        return Float.parseFloat(roundedNumber);
    }
}
