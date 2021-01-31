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

        List<Record> records = buildAndGetRecord(products,timestamp);
        storeRecords(records,timestamp);

        ParserResult res = new ParserResult();
        res.invoiceDate = timestamp;
        res.records = records;
        return  res;
    }

    private List<Record> buildAndGetRecord(List<Product> products, Timestamp timestamp) {
        List<Record> records = new ArrayList<>();

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
            record.setpDate(timestamp);
            records.add(record);
        });
        setSCodes(records);
        setNewPrices(records);
        return records;
    }

    private void storeRecords(List<Record> records, Timestamp invoiceDate) {
        boolean hasImported = recordsRepository.hasBeenImported(invoiceDate);
        if( !hasImported ){
            recordsRepository.storeRecords(records);
        }else {
            System.err.println("Invoice "+ invoiceDate+" has been imported multiple times");
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

    private void setNewPrices(List<Record> records){
        List<Mapping> mappings = mappingsRepository.getMappings();
        List<Rule> rules = rulesRepository.getRules();

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            float newPrice =
                    priceCalculator.getNewPrice
                    (
                        record.getName(),
                        record.getPrice().floatValue(),
                        mappings,
                        rules
                    );
                records.get(i).setNewPrice(newPrice);
        }
    }

    private void setDate(List<Product> records, Timestamp invoiceDate){
        for (int i = 0; i < records.size(); i++) {
            records.get(i).setpDate(invoiceDate);
        }
    }


}
