package application.domain.importer.services;

import application.controllers.dtos.ImportDTO;
import application.domain.importer.Product;
import application.domain.importer.parser.IParsers;
import application.domain.importer.parser.ParsersFactory;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.Record;
import application.model.record.services.IRecordsRepository;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("tableCreator")
public class TableComposer implements ITableComposer {

    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IMappingsRepository mappingsRepository;
    @Autowired
    IRecordsRepository recordsRepo;
    @Autowired
    IRetailPricesRepository retailPricesRepo;
    @Autowired
    IRecordsRepository recordsRepository;
    @Autowired
    ParsersFactory factory;

    @Override
    public ImportDTO createTable(String invoiceContent) {

        ImportDTO response = new ImportDTO();
        List<Record> records = null;
        try {
            IParsers docParser = getParser(invoiceContent);
            List<Product> products = docParser.parse(invoiceContent);
            Timestamp timestamp = docParser.getTimeStamp(invoiceContent);

            records = recordsRepository.buildAndGetRecords(products,timestamp);
            recordsRepository.storeRecords(records,timestamp);

            response.invoiceDate = timestamp.toString();


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

        List<Rule> rules = rulesRepository.getRules();
        List<Mapping> mappings = mappingsRepository.getMappings();
        List<String> sCodes = rules
                .stream()
                .map(Rule::getsCode)
                .collect(Collectors.toList());

        List<Smast> smastList = retailPricesRepo.getRetailPrices(sCodes);
        Map<String, List<Float>> latestPrices = recordsRepo
                .getLatestNewPrices(
                        records
                        .stream()
                        .map(Record::getsCode)
                        .collect(Collectors.toList())
                );


        records.forEach(x -> {

            Rule rule = getRule(x.getName(), mappings, rules);
            String sCode = rule.getsCode();
            Smast smast = getKefalaioData(sCode, smastList);

            ImportDTO.Entry r = response.new Entry();
            r.name = x.getName();
            r.invoicePrice = x.getPrice().floatValue();
            r.profitPercentage = rule.getProfitPercentage();
            r.origin = x.getOrigin();
            r.retailPrice = smast.getsRetailPrice();
            r.sCode = rule.getsCode();
            r.number = x.getNumber();
            r.newPrice = x.getNewPrice();
            r.profitInEuro = getActualProfit(r.newPrice.floatValue(),r.invoicePrice);
            r.records = latestPrices.get(x.getsCode());

            response.data.add(r);

        });

        return response;
    }

    private IParsers getParser(String invoiceContent){
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            return factory.getService("savakis");
        } else {
            return factory.getService("kapnisis");
        }
    }


    private ImportDTO.Error getError(ImportDTO.ErrorCode code, String msg){
        ImportDTO.Error err  = new ImportDTO.Error();
        err.code = code;
        err.msg = msg;
        return err;
    }

    private Rule getRule(String sName, List<Mapping> mappings, List<Rule> rules){
        String sCode = mappings
                .stream()
                .filter(x->x.getpName().equals(sName))
                .findFirst()
                .get().getsCode();
        Optional<Rule> rule = rules.stream()
                .filter(x->x.getsCode().equals(sCode))
                .findFirst();
        if(!rule.isPresent()){
            throw new NoSuchElementException("Failed to retrieve sCode for "+sName);
        }
        return rule.get();
    }

    private Smast getKefalaioData(String sCode, List<Smast> smastList ) {
        Optional<Smast> smast = smastList
                .stream()
                .filter(x -> x.getsCode().equals(sCode))
                .findFirst();
        if(!smast.isPresent()){
            throw new NoSuchElementException("Failed to retrieve retail price for "+sCode);
        }
        return smast.get();
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
