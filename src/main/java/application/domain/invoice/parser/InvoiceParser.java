package application.domain.invoice.parser;

import application.model.invoice.Invoice;
import application.model.invoice.InvoiceRow;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("invoiceParser")
public class InvoiceParser implements IInvoiceParser {
    List<InvoiceRow> invoiceRows = new ArrayList<>();

    public Invoice getInvoice(String invoiceContent){
        Invoice invoice = new Invoice();
        String[] lines = invoiceContent.split("\n");
        boolean isReading = false;

        for( int i=0; i < lines.length;++i){
            String line = lines[i];
            if(i==2 || i == 1 ){ //date is some times at the second line and some times on third based on input file.
                invoice.date =  getDateTime(line);
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
                    addProductToList(line);
                }catch (Exception e){
                    //TODO: log exception
                    e.printStackTrace();
                    //JOptionPane.showMessageDialog(null,"Tο φόρτωμα του αρχείου παρουσίασε πρόβλημα\n" +
                     //       "παρακαλώ επαληθεύσετε οτι τα δεδομένα είναι σωστά.","Διαφορετική μορφή κειμένου στο αρχείου",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        invoice.invoiceRows = invoiceRows;
        return invoice;
    }



    private Date getDateTime(String line)
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

    private Date getDateTimeFrom(String dateTime)
    {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
        try {
            date = formatter.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private boolean ShouldStartReadContent(String line) {
        return line.contains("ΣΧΕΤΙΚΑ ΠΑΡΑΣΤΑΤΙΚΑ") ||  line.contains("Εκ Μεταφοράς");
    }

    private boolean ShouldSkipContent(String line) {
        return line.contains("ΥΠΟΛΟΙΠΑ") || line.contains("Σε µεταφορά");
    }

    private void addProductToList(String productLine) throws  Exception{

        productLine=productLine.replace('∆','Δ');
        productLine=productLine.replace('Ω','Ω');

        if(productLine.contains("ΤΕΛΑΡΑ")
                || productLine.contains("ΚΕΝΑ")){
            return;
        }

        InvoiceRow invoiceRow = new InvoiceRow();

        String[] line;
        if(productLine.contains("ΚΙΛ ")){
            line = productLine.split("ΚΙΛ ");
            invoiceRow.measurement_unit = "ΚΙΛ ";
        } else if(productLine.contains("ΤΕΜ ")){
            line =  productLine.split("ΤΕΜ ");
            invoiceRow.measurement_unit = "TEM ";
        } else if(productLine.contains("ΜΑΤ ")) {
            line =  productLine.split("ΜΑΤ ");
            invoiceRow.measurement_unit =  "ΜΑΤ ";
        } else if(productLine.contains("ΖΕΥ ")) {
            line =  productLine.split("ΖΕΥ ");
            invoiceRow.measurement_unit =  "ΖΕΥ ";
        } else {
            System.err.println(productLine);
            throw new Exception("Error: failed to retrieve measurement unit");
        }

        String[] subLine2 = line[0].split(" ");
        String[] subLine3 = line[1].trim().split(" ");

        invoiceRow.number = subLine2[subLine2.length-1]; //hack in order to extract number.
        invoiceRow.name    = line[0].split("-")[0];
        invoiceRow.origin  = (line[0].split("-")[1]).split(" ")[0];

        //special case which is necessary to fix invoice name
        if(productLine.contains("ΠΛΑΚΕ")){
            invoiceRow.name = invoiceRow.name +" "+"ΠΛΑΚΕ";
        }

        invoiceRow.quantity = Double.parseDouble(subLine3[1].replace(",","."));
        invoiceRow.invoicePrice   = Double.parseDouble(subLine3[2].replace(",","."));
        invoiceRow.discount= Double.parseDouble(subLine3[4].replace(",","."));
        invoiceRow.tax      =Integer.parseInt(subLine3[6].replace(",","."));
        invoiceRows.add(invoiceRow);
    }


}
