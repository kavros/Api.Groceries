package application.domain.importer.parser;

import application.domain.importer.Product;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class SavakisParser implements IParsers {

    @Override
    public String getType() {
        return "savakis";
    }

    @Override
    public Timestamp getTimeStamp(String doc) throws ParseException  {
        String[] lines = doc.split("\n");
        Timestamp dateTime = null;
        String date=null,time =null;
        for( int i=0; i < lines.length;++i) {
            if( date == null && lines[i].contains("Ημερομηνία") ) {
                date = lines[i].split(":")[1].trim();
            } else if (time == null && lines[i].contains("ΠΑΝΤΟΠΩΛΕΙΟΥΛΙΑ")){
                time = lines[i].split(" ")[1].trim();
            }
            if(date != null && time != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                dateTime = new java.sql.Timestamp(
                        formatter.parse(date +" "+ time).getTime()
                );
                break;
            }
        }

        return dateTime;
    }

    @Override
    public List<Product> parse(String invoiceContent) {
        List<Product> list = new ArrayList<>();
        String[] lines = invoiceContent.split("\n");
        boolean shouldRead = false;
        for( int i=0; i < lines.length;++i) {
           if (lines[i].contains("ΑΞΙΑ %")) {
                shouldRead = true;
                continue;
            } else if(
                    lines[i].contains("ΣΥΝΟΛΑ") ||
                            lines[i].contains("ΑΝΑΛΥΣΗ") ||
                            lines[i].contains("ΕΠΩΝΥΜΙΑ")
            ) {
                shouldRead = false;
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
                    product.setNumber(cols[offset+3].trim());
                } else if (cols.length == 11) {
                    product.setName(cols[1].trim());
                    product.setOrigin(cols[2].trim());
                    product.setNumber(cols[offset+3].trim());
                } else if(cols.length == 13) {
                    product.setName(cols[1] + " " + cols[2].trim());
                    product.setOrigin(cols[4].trim());
                    offset = 2;
                    product.setNumber(cols[offset+3].trim());
                } else if (cols.length == 10) {
                    // missing product number
                    product.setName(cols[1].trim());
                    product.setOrigin(cols[2].trim());
                    offset = -1;
                    product.setNumber("N/A");
                } else if(cols.length == 9) {
                    // missing origin and product number
                    product.setName(cols[1].trim());
                    product.setOrigin(" ");
                    offset = -2;
                    product.setNumber(" ");
                }

                product.setQuantity(cols[offset+6].trim());
                product.setPrice(cols[offset+7].trim());
                product.setTax(cols[offset+8].trim());
                product.setDiscount(cols[offset+10].trim());
                product.setMeasurementUnit("-");
                list.add(product);
            }
        }

        return list;
    }


}