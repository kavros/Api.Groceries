package application.domain.importer.parser;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class KapnisisParser implements IParsers {

    @Override
    public ParserType getType() {
        return ParserType.Kapnisis;
    }



    @Override
    public List<Product> parse(String invoiceContent) {
        String[] lines = invoiceContent.split("\n");
        boolean isReading = false;
        List<Product> products = new ArrayList<>();
        for( int i=0; i < lines.length;++i){
            String line = lines[i];

            if(shouldStartReadContent(line)) {
                isReading = true;
                continue;
            }

            if(shouldSkipContent(line)) {
                isReading=false;
            }

            if(isReading){
                try {
                    addProductToList(line,products);
                } catch (IllegalArgumentException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        return  products;
    }



    private boolean shouldStartReadContent(String line) {
        return line.contains("ΣΧΕΤΙΚΑ ΠΑΡΑΣΤΑΤΙΚΑ") ||  line.contains("Εκ Μεταφοράς");
    }

    private boolean shouldSkipContent(String line ) {
        return line.contains("ΥΠΟΛΟΙΠΑ") || line.contains("Σε µεταφορά");
    }

    private void addProductToList(String productLine, List<Product> records) throws  IllegalArgumentException{

        productLine=productLine.replace('∆','Δ');
        productLine=productLine.replace('Ω','Ω');

        if(productLine.contains("ΤΕΛΑΡΑ")
                || productLine.contains("ΚΕΝΑ")){
            return;
        }

        Product record = new Product();

        String[] line;
        if(productLine.contains("ΚΙΛ ")){
            line = productLine.split("ΚΙΛ ");
            record.setMeasurementUnit( "ΚΙΛ ");
        } else if(productLine.contains("ΤΕΜ ")){
            line =  productLine.split("ΤΕΜ ");
            record.setMeasurementUnit ("TEM ");
        } else if(productLine.contains("ΜΑΤ ")) {
            line =  productLine.split("ΜΑΤ ");
            record.setMeasurementUnit("ΜΑΤ ");
        } else if(productLine.contains("ΖΕΥ ")) {
            line =  productLine.split("ΖΕΥ ");
            record.setMeasurementUnit("ΖΕΥ ");
        } else {
            throw new IllegalArgumentException("Error: failed to retrieve measurement unit for line\n"+ productLine);
        }

        String[] subLine2 = line[0].split(" ");
        String[] subLine3 = line[1].trim().split(" ");

        record.setNumber( subLine2[subLine2.length-1]); //hack in order to extract number.
        record.setName( line[0].split("-")[0]);
        record.setOrigin ((line[0].split("-")[1]).split(" ")[0]);

        //special case which is necessary to fix invoice name
        if(productLine.contains("ΠΛΑΚΕ")){
            record.setName(record.getName() +" "+"ΠΛΑΚΕ");
        } else if(productLine.contains("ΠΑΤΑΤΕΣ-ΘΗΒΑΣ")) {
            record.setName("ΠΑΤΑΤΕΣ-ΘΗΒΑΣ");
        }

        record.setQuantity( subLine3[1].replace(",","."));
        record.setPrice( subLine3[2].replace(",","."));
        record.setDiscount(subLine3[4].replace(",","."));
        record.setTax( subLine3[6].replace(",",".") );

        records.add(record);
    }

}
