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
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Component("invoiceParser")
public class InvoiceParser implements IInvoiceParser {

    @Autowired
    NewPriceCalculator newPriceCalculator;

    public ParserResult parseAndLoad(String invoiceContent) throws ParseException {
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
