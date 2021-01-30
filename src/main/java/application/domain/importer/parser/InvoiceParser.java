package application.domain.importer.parser;

import application.domain.importer.Product;
import application.domain.importer.price_calculator.PriceCalculator;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.Record;
import application.model.record.services.IRecordsRepository;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Component("invoiceParser")
public class InvoiceParser implements IInvoiceParser {

    @Autowired
    PriceCalculator priceCalculator;
    @Autowired
    IMappingsRepository mappingsRepository;
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    ParsersFactory factory;
    @Autowired
    IRecordsRepository recordsRepository;

    public InvoiceParser(IRulesRepository rules,
                         IMappingsRepository mappings,
                         PriceCalculator calc,
                         ParsersFactory theFactory,
                         IRecordsRepository recordsRepo) {
        rulesRepository = rules;
        mappingsRepository  = mappings;
        priceCalculator = calc;
        factory = theFactory;
        recordsRepository = recordsRepo;
    }

    private IParsers getParser(String invoiceContent){
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            return factory.getService("savakis");
        } else {
            return factory.getService("kapnisis");
        }
    }

    public ParserResult parseAndLoad(String invoiceContent) throws ParseException {

        IParsers docParser = getParser(invoiceContent);
        List<Product> products = docParser.parse(invoiceContent);
        Timestamp timestamp = docParser.getTimeStamp(invoiceContent);

        ParserResult res = new ParserResult();
        res.invoiceDate = timestamp;
        products.forEach(x -> {
            Record record = new Record();
            record.setName(x.getName());
            record.setNumber(x.getNumber());
            record.setOrigin(x.getOrigin());
            record.setDiscount(x.getDiscount().toString());
            record.setQuantity(x.getQuantity().toString());
            record.setTax(x.getTax());
            record.setPrice(x.getPrice().toString());
            record.setMeasurement_unit(x.getMeasurementUnit());
            res.records.add(record);
        });
        setCommonValues(res);
        storeInvoice(res);
        return  res;
    }


    private void setCommonValues(ParserResult res){
        setDate(res);
        setSCodes(res.records);
        setNewPrices(res);
    }

    private void storeInvoice(ParserResult res){
        List<String> warnings = res.warnings;

        boolean hasImported = recordsRepository.hasBeenImported(res.invoiceDate);
        if( !hasImported ){
            recordsRepository.storeRecords(res.records);
        }else {
            warnings.add("Invoice "+res.invoiceDate+" has been imported multiple times");
        }
    }

    private void setSCodes(List<Record> records) {
        List<Mapping> mappings = mappingsRepository.getMappings();
        List<String> missingSettings = new ArrayList();
        for(Record record: records){
            Optional<Mapping> mapping =  getMappingFor(record.getName(), mappings);
            if(!mapping.isPresent()){
                missingSettings.add(record.getName());
            }else{
                record.sCode = mapping.get().getsCode();
            }
        }
        if(!missingSettings.isEmpty())
            throw new NoSuchElementException("Failed to retrieve sCode for " + missingSettings);
    }

    private Optional<Mapping> getMappingFor(String pName, List<Mapping> mappings){
        return mappings
                .stream()
                .filter(s -> pName.equals(s.getpName()))
                .findFirst();
    }

    private void setNewPrices(ParserResult res){
        List<Mapping> mappings = mappingsRepository.getMappings();
        List<Rule> rules = rulesRepository.getRules();
        for (int i = 0; i < res.records.size(); i++) {
            Record record = res.records.get(i);
            float newPrice =
                    priceCalculator.getNewPrice
                    (
                        record.getName(),
                        record.getPrice().floatValue(),
                        mappings,
                        rules
                    );
                res.records.get(i).setNewPrice(newPrice);
        }
    }

    private void setDate(ParserResult res){
        for (int i = 0; i < res.records.size(); i++) {
            res.records.get(i).setpDate(res.invoiceDate);
        }
    }


}
