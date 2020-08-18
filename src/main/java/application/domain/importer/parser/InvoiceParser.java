package application.domain.importer.parser;

import application.domain.importer.price_calculator.NewPriceCalculator;
import application.hibernate.HibernateUtil;
import application.model.records.Product;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("invoiceParser")
public class InvoiceParser implements IInvoiceParser {

    @Autowired
    NewPriceCalculator newPriceCalculator;

    public ParserResult parseAndLoad(String invoiceContent) throws ParseException {
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            return parseInvoice1(invoiceContent);
        } else {
            return parseInvoice2(invoiceContent);
        }


    }
    private ParserResult parseInvoice1(String invoiceContent) throws ParseException{
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
                Product product = new Product();

                if( cols.length == 12 ) {
                    product.setOrigin(cols[3].trim());
                    product.setName(cols[1] + " " + cols[2].trim());
                    offset = 1;
                } else if (cols.length == 11) {
                    product.setName(cols[1].trim());
                    product.setOrigin(cols[2].trim());
                } else if(cols.length == 13) {
                    product.setName(cols[1] + " " + cols[2].trim());
                    product.setOrigin(cols[4].trim());
                    offset = 2;
                }

                product.setNumber(cols[offset+3].trim());
                product.setQuantity(cols[offset+6].trim());
                product.setPrice(cols[offset+7].trim());
                product.setTax(cols[offset+8].trim());
                product.setDiscount(cols[offset+10].trim());
                product.setMeasurement_unit("-");
                product.setpDate(dateTime);
                res.products.add(product);
            }
        }
        res.invoiceDate = dateTime;
        setNewPrices(res);
        storeInvoice(res.products,res.warnings);
        System.out.println(res.products);
        return res;
    }

    private ParserResult parseInvoice2(String invoiceContent) throws ParseException{
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
                    addProductToList(line,res.products);
                } catch (IllegalArgumentException ex) {
                    res.warnings.add(ex.getMessage());
                }
            }
        }

        res.invoiceDate = new java.sql.Timestamp(date.getTime());

        setDate(res);
        setNewPrices(res);
        storeInvoice(res.products,res.warnings);

        return  res;
    }

    private void setNewPrices(ParserResult res){
        for (int i = 0; i < res.products.size(); i++) {
            Product product = res.products.get(i);
            float newPrice =
                    newPriceCalculator.getNewPrice
                    (
                        product.getName(),
                        product.getPrice()
                    );
                res.products.get(i).setNewPrice(newPrice);
        }
    }

    private void setDate(ParserResult res){
        for (int i = 0; i < res.products.size(); i++) {
            res.products.get(i).setpDate(res.invoiceDate);
        }
    }

    private void storeInvoice(List<Product> products, List<String> warnings){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();


        Timestamp invoiceDate = products.get(0).getpDate();
        if( !hasBeenImported(invoiceDate, session)){
            for (int i = 0; i < products.size(); i++) {

                session.save(products.get(i));
            }
        }else {
            warnings.add(new String("Invoice "+invoiceDate+" has imported already"));
        }

        session.getTransaction().commit();

    }

    private boolean hasBeenImported(Timestamp invoiceDate, Session session){

        Query query = session.createQuery("from Product" );
        List<Product> records = query.list();
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

    private void addProductToList(String productLine, List<Product> products) throws  IllegalArgumentException{

        productLine=productLine.replace('∆','Δ');
        productLine=productLine.replace('Ω','Ω');

        if(productLine.contains("ΤΕΛΑΡΑ")
                || productLine.contains("ΚΕΝΑ")){
            return;
        }

        Product product = new Product();

        String[] line;
        if(productLine.contains("ΚΙΛ ")){
            line = productLine.split("ΚΙΛ ");
            product.measurement_unit = "ΚΙΛ ";
        } else if(productLine.contains("ΤΕΜ ")){
            line =  productLine.split("ΤΕΜ ");
            product.measurement_unit = "TEM ";
        } else if(productLine.contains("ΜΑΤ ")) {
            line =  productLine.split("ΜΑΤ ");
            product.measurement_unit =  "ΜΑΤ ";
        } else if(productLine.contains("ΖΕΥ ")) {
            line =  productLine.split("ΖΕΥ ");
            product.measurement_unit =  "ΖΕΥ ";
        } else {
            throw new IllegalArgumentException("Error: failed to retrieve measurement unit for line\n"+ productLine);
        }

        String[] subLine2 = line[0].split(" ");
        String[] subLine3 = line[1].trim().split(" ");

        product.number = subLine2[subLine2.length-1]; //hack in order to extract number.
        product.setName( line[0].split("-")[0]);
        product.origin  = (line[0].split("-")[1]).split(" ")[0];

        //special case which is necessary to fix invoice name
        if(productLine.contains("ΠΛΑΚΕ")){
            product.setName(product.getName() +" "+"ΠΛΑΚΕ");
        }

        product.quantity = Double.parseDouble(subLine3[1].replace(",","."));
        product.price   = Float.parseFloat(subLine3[2].replace(",","."));
        product.discount= Double.parseDouble(subLine3[4].replace(",","."));
        product.tax      =Integer.parseInt(subLine3[6].replace(",","."));

        products.add(product);
    }


}
