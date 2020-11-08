package application.domain.importer.parser;

import application.domain.importer.price_calculator.PriceCalculator;
import application.hibernate.HibernateUtil;
import application.model.mappings.Mappings;
import application.model.mappings.services.IMappingsRepository;
import application.model.records.Record;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("invoiceParser")
public class InvoiceParser implements IInvoiceParser {

    @Autowired
    PriceCalculator priceCalculator;
    @Autowired
    IMappingsRepository mappingsRepository;
    @Autowired
    IRulesRepository rulesRepository;

    public ParserResult parseAndLoad(String invoiceContent) throws ParseException {
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            return parseInvoiceSavaki(invoiceContent);
        } else {
            return parseInvoiceKapnisi(invoiceContent);
        }
    }

    private ParserResult parseInvoiceSavaki(String invoiceContent) throws ParseException{
        ParserResult res = new ParserResult();
        String[] lines = invoiceContent.split("\n");
        Timestamp dateTime = null;
        String date=null,time =null;

        boolean shouldRead = false;
        for( int i=0; i < lines.length;++i) {
            if( date == null && lines[i].contains("Ημερομηνία") ) {
                date = lines[i].split(":")[1].trim();
            } else if (time == null && lines[i].contains("ΠΑΝΤΟΠΩΛΕΙΟΥΛΙΑ")){
                time = lines[i].split(" ")[1].trim();
            } else if (lines[i].contains("ΑΞΙΑ %")) {
                shouldRead = true;
                continue;
            } else if(lines[i].contains("ΣΥΝΟΛΑ") || lines[i].contains("ΑΝΑΛΥΣΗ")) {
                shouldRead = false;
            }
            if(date != null && time != null && dateTime == null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                dateTime = new java.sql.Timestamp(
                                                formatter.parse(date +" "+ time).getTime()
                                                );
            }
            if(shouldRead) {
                String[] cols = lines[i].split(" ");
                int offset = 0;
                if(cols.length < 9) {
                    continue;
                }
                Record record = new Record();

                if( cols.length == 12 ) {
                    record.setOrigin(cols[3].trim());
                    record.setName(cols[1] + " " + cols[2].trim());
                    offset = 1;
                } else if (cols.length == 11) {
                    record.setName(cols[1].trim());
                    record.setOrigin(cols[2].trim());
                } else if(cols.length == 13) {
                    record.setName(cols[1] + " " + cols[2].trim());
                    record.setOrigin(cols[4].trim());
                    offset = 2;
                }

                record.setNumber(cols[offset+3].trim());
                record.setQuantity(cols[offset+6].trim());
                record.setPrice(cols[offset+7].trim());
                record.setTax(cols[offset+8].trim());
                record.setDiscount(cols[offset+10].trim());
                record.setMeasurement_unit("-");
                res.records.add(record);
            }
        }
        res.invoiceDate = dateTime;

        setValuesAndStore(res);
        return res;
    }

    private ParserResult parseInvoiceKapnisi(String invoiceContent) throws ParseException{
        ParserResult res = new ParserResult();
        String[] lines = invoiceContent.split("\n");
        boolean isReading = false;
        Date date = new Date();
        for( int i=0; i < lines.length;++i){
            String line = lines[i];
            if(i==2 || i == 1 ){ //date is some times at the second line and some times on third based on input file.
                date = getDateAndTime1(line);
            }

            if(ShouldStartReadContent(line)) {
                isReading = true;
                continue;
            }

            if(ShouldSkipContent(line)) {
                isReading=false;
            }

            if(isReading){
                try {
                    addProductToList(line,res.records);
                } catch (IllegalArgumentException ex) {
                    res.warnings.add(ex.getMessage());
                }
            }
        }

        res.invoiceDate = new java.sql.Timestamp(date.getTime());

        setValuesAndStore(res);

        return  res;
    }

    private void setValuesAndStore(ParserResult res){
        setDate(res);
        setSCodes(res);
        setNewPrices(res);

        List<Record> records = res.records;
        List<String> warnings = res.warnings;
        Session session = HibernateUtil.getSessionFactory().openSession();
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

    }

    private void setSCodes(ParserResult res) {
        List<Mappings> mappings = mappingsRepository.getMappings();
        List<String> missingSettings = new ArrayList();
        for(Record record: res.records){
            Optional<Mappings> mapping = mappings
                    .stream()
                    .filter(s -> record.getName().equals(s.getsName()))
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
        List<Mappings> mappings = mappingsRepository.getMappings();
        List<Rules> rules = rulesRepository.getRules();
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

    private Date getDateAndTime1(String line) throws ParseException {
        Date date = null;
        if(line.contains("/") && !line.contains("ΗΜ/ΝΙΑ")) {
            line = line.replace("  ", " ");//trim in between double spaces.
            String[] array = line.split(" ");
            String dateTimeString =  array[2] + " " +array[3];
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
            date = formatter.parse(dateTimeString);
        }
        return date;
    }

    private boolean ShouldStartReadContent(String line) {
        return line.contains("ΣΧΕΤΙΚΑ ΠΑΡΑΣΤΑΤΙΚΑ") ||  line.contains("Εκ Μεταφοράς");
    }

    private boolean ShouldSkipContent(String line ) {
        return line.contains("ΥΠΟΛΟΙΠΑ") || line.contains("Σε µεταφορά");
    }

    private void addProductToList(String productLine, List<Record> records) throws  IllegalArgumentException{

        productLine=productLine.replace('∆','Δ');
        productLine=productLine.replace('Ω','Ω');

        if(productLine.contains("ΤΕΛΑΡΑ")
                || productLine.contains("ΚΕΝΑ")){
            return;
        }

        Record record = new Record();

        String[] line;
        if(productLine.contains("ΚΙΛ ")){
            line = productLine.split("ΚΙΛ ");
            record.measurement_unit = "ΚΙΛ ";
        } else if(productLine.contains("ΤΕΜ ")){
            line =  productLine.split("ΤΕΜ ");
            record.measurement_unit = "TEM ";
        } else if(productLine.contains("ΜΑΤ ")) {
            line =  productLine.split("ΜΑΤ ");
            record.measurement_unit =  "ΜΑΤ ";
        } else if(productLine.contains("ΖΕΥ ")) {
            line =  productLine.split("ΖΕΥ ");
            record.measurement_unit =  "ΖΕΥ ";
        } else {
            throw new IllegalArgumentException("Error: failed to retrieve measurement unit for line\n"+ productLine);
        }

        String[] subLine2 = line[0].split(" ");
        String[] subLine3 = line[1].trim().split(" ");

        record.number = subLine2[subLine2.length-1]; //hack in order to extract number.
        record.setName( line[0].split("-")[0]);
        record.origin  = (line[0].split("-")[1]).split(" ")[0];

        //special case which is necessary to fix invoice name
        if(productLine.contains("ΠΛΑΚΕ")){
            record.setName(record.getName() +" "+"ΠΛΑΚΕ");
        }

        record.setQuantity( subLine3[1].replace(",","."));
        record.setPrice( subLine3[2].replace(",","."));
        record.setDiscount(subLine3[4].replace(",","."));
        record.tax      =Integer.parseInt(subLine3[6].replace(",","."));

        records.add(record);
    }


}
