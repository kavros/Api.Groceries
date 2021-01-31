package application.model.record.services;

import application.domain.importer.Product;
import application.domain.importer.price_calculator.PriceCalculator;
import application.hibernate.IHibernateUtil;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.Record;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Component("recordsRepository")
public class RecordsRepository implements IRecordsRepository {
    @Autowired
    IHibernateUtil dbConnection;
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IMappingsRepository mappingsRepository;

    @Autowired
    PriceCalculator priceCalculator;


    public void updatePrices(List<Map.Entry<String, BigDecimal>> data, String invoiceDate)
    {
        Session session = dbConnection.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query query = session
                .createQuery("update Record set newPrice= :new_price " +
                        "where name= :product_name and pDate= :invoice_date");
        for(Map.Entry<String, BigDecimal> entry:data) {
            query.setParameter("new_price", entry.getValue());
            query.setParameter("product_name", entry.getKey());
            query.setParameter("invoice_date", invoiceDate);
            query.executeUpdate();
        }
        tx.commit();
        session.close();
    }
    
    public Map<String,List<Float>> getLatestNewPrices(List<String> sCodes) {
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        List<Record> records = session
                .createQuery("from Record order by pDate desc" )
                .list();
        session.close();

        Map<String,List<Float>>  map  = new HashMap<>();
        for(String sCode : sCodes) {
            List<Float> latestNewPrices = records
                    .stream()
                    .filter(
                            x -> x.getsCode().equals(sCode)
                    )
                    .map(x -> x.getNewPrice().floatValue())
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(sCode,latestNewPrices);
        }
        return map;
    }

    public Map<String, List<Float>> getLatestInvoicePrices(List<String> sCodes) {
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Record order by pDate desc" );
        List<Record> records = query.list();
        session.close();
        Map<String,List<Float>>  map  = new HashMap<>();
        for(String sCode : sCodes) {
            List<Float> latestInvoicePrices = records
                    .stream()
                    .filter(
                            x -> x.getsCode().equals(sCode)
                    )
                    .map(x -> x.getPrice().floatValue())
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(sCode,latestInvoicePrices);
        }
        return map;
    }

    public List<Record> buildAndGetRecords(List<Product> products, Timestamp timestamp) {
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


    public void storeRecords(List<Record> records, Timestamp invoiceDate) {
        boolean hasImported = hasBeenImported(invoiceDate);
        if( !hasImported ){
            this.storeRecords(records);
        }else {
            System.err.println("Invoice "+ invoiceDate+" has been imported multiple times");
        }
    }

    private boolean hasBeenImported(Timestamp invoiceDate){
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Record" );
        List<Record> records = query.list();
        long entries =records.stream().filter(x -> x.getpDate().equals(invoiceDate)).count();

        return entries > 0;
    }

    private void storeRecords(List<Record> records){
        Session session = dbConnection.getSessionFactory().openSession();
        session.beginTransaction();

        for (int i = 0; i < records.size(); i++) {
            session.save(records.get(i));
        }
        session.getTransaction().commit();
        session.close();
    }
}
