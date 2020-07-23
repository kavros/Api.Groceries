package application.model.records.services;

import application.domain.upload.services.NewPriceCalculator;
import application.hibernate.HibernateUtil;
import application.model.records.Product;
import application.model.records.ProductId;
import application.model.smast.Smast;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Component("recordsRepository")
public class RecordsRepository implements IRecordsRepository {

    @Autowired
    NewPriceCalculator newPriceCalculator;

    public List<Product> getProducts(Timestamp timestamp) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Product" );
        List<Product> records = query.list();

        return records
                .stream()
                .filter(x->x.getpDate().equals(timestamp))
                .collect(Collectors.toList());
    }

    public void updatePrices(List<Map.Entry<String, Float>> data, String invoiceDate)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();

       /*try{
           ProductId id =  new ProductId();
           id.setName("ΑΓΓΟΥΡΙΑ");
           //"2020-04-07 06:39:00";


           DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
           Date date = null;

           date = dateFormat.parse("2020-04-07 06:39:00");
           long time = date.getTime();
           id.setpDate(new Timestamp(time));
           Product p =  (Product) session.get(Product.class, id);
           Hibernate.initialize(p);

           System.out.println(p);
       }catch(ParseException ex){

       }*/




//        Criteria criteria  = session.createCriteria(Product.class);
//        List<String> productNames = new ArrayList<>();
//        for(Map.Entry<String, Float> entry: data){
//            productNames.add(entry.getKey());
//        }
//        criteria.add(Restrictions.in("name", productNames));
//        criteria.add(Restrictions.eq("pDate", invoiceDate));
//        List<Product> products = criteria.list();

//            Date date = getDateTime(invoiceDate);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            Date parsedDate = dateFormat.parse(invoiceDate);
//            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            //System.out.println(records);

        //session.getTransaction().commit();
        HibernateUtil.shutdown();
    }

    public ParserResult parseInvoice(String invoiceContent) throws ParseException {
        ParserResult res = new ParserResult();

        String[] lines = invoiceContent.split("\n");
        boolean isReading = false;
        Date date = new Date();
        for( int i=0; i < lines.length;++i){
            String line = lines[i];
            if(i==2 || i == 1 ){ //date is some times at the second line and some times on third based on input file.
                date = getDateTime(line);
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
            float newPrice = newPriceCalculator.getNewPrice(product.getName(),product.getPrice());
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
        HibernateUtil.shutdown();
    }

    private boolean hasBeenImported(Timestamp invoiceDate, Session session){

        Query query = session.createQuery("from Product" );
        List<Product> records = query.list();
        long entries =records.stream().filter(x -> x.getpDate().equals(invoiceDate)).count();

        return entries > 0;
    }

    private Date getDateTime(String line) throws ParseException
    {
        Date date = null;
        if(line.contains("/") && !line.contains("ΗΜ/ΝΙΑ")) {
            line = line.replace("  ", " ");//trim in between double spaces.
            String[] array = line.split(" ");
            String dateTimeString =  array[2] + " " +array[3];

            date = getDateTimeFrom(dateTimeString);
        }
        return date;
    }

    private Date getDateTimeFrom(String dateTime) throws ParseException
    {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

        date = formatter.parse(dateTime);

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


    public Map<String,List<Float>> getLatestPrices(List<String> productNames) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Product order by pDate desc" );
        List<Product> records = query.list();

        Map<String,List<Float>>  map  = new HashMap<>();
        for(String targetName : productNames) {
            List<Float> latestInvoicePrices = records
                    .stream()
                    .filter(
                            x -> x.getName().equals(targetName)
                    )
                    .map(x -> x.newPrice)
                    .limit(3)
                    .collect(Collectors.toList());
            map.put(targetName,latestInvoicePrices);
        }
        return map;
    }


}
