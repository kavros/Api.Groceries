package application.domain.importer.parser;

import application.model.record.Record;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component("kapnisisParser")
public class KapnisisParser implements IParsers {

    public ParserResult parse(String invoiceContent) throws ParseException{
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

        return  res;
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
