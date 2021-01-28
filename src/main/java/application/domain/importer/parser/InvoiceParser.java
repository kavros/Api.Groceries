package application.domain.importer.parser;

import application.domain.importer.price_calculator.PriceCalculator;
import application.hibernate.IHibernateUtil;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.Record;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
    IHibernateUtil dbConnection;
    @Autowired
    IParsers savakisParser;
    @Autowired
    IParsers kapnisisParser;

    public InvoiceParser(IRulesRepository rules,
                         IMappingsRepository mappings,
                         PriceCalculator calc,
                         IHibernateUtil dbCon,
                         IParsers parser) {
        this.rulesRepository = rules;
        this.mappingsRepository  = mappings;
        this.priceCalculator = calc;
        this.dbConnection = dbCon;
        if(parser instanceof  KapnisisParser){
            kapnisisParser = parser;
        }else{
            savakisParser = parser;
        }


    }
    public ParserResult parseAndLoad(String invoiceContent) throws ParseException {
        ParserResult res;
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            res =  savakisParser.parse(invoiceContent);
        } else {
            res =  kapnisisParser.parse(invoiceContent);
        }
        setValuesAndStore(res);
        return  res;
    }

    private void setValuesAndStore(ParserResult res){
        setDate(res);
        setSCodes(res);
        setNewPrices(res);

        List<Record> records = res.records;
        List<String> warnings = res.warnings;
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        Timestamp invoiceDate = records.get(0).getpDate();
        if( !hasBeenImported(invoiceDate, session)){
            for (int i = 0; i < records.size(); i++) {

                session.save(records.get(i));
            }
        }else {
            warnings.add("Invoice "+invoiceDate+" has been imported multiple times");
        }

        session.getTransaction().commit();
        session.close();
    }

    private void setSCodes(ParserResult res) {
        List<Mapping> mappings = mappingsRepository.getMappings();
        List<String> missingSettings = new ArrayList();
        for(Record record: res.records){
            Optional<Mapping> mapping = mappings
                    .stream()
                    .filter(s -> record.getName().equals(s.getpName()))
                    .findFirst();
            if(!mapping.isPresent()){
                missingSettings.add(record.getName());
            }else{
                record.sCode = mapping.get().getsCode();
            }
        }
        if(!missingSettings.isEmpty())
            throw new NoSuchElementException("Failed to retrieve sCode for " + missingSettings);
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

    private boolean hasBeenImported(Timestamp invoiceDate, Session session){

        Query query = session.createQuery("from Record" );
        List<Record> records = query.list();
        long entries =records.stream().filter(x -> x.getpDate().equals(invoiceDate)).count();

        return entries > 0;
    }
}
