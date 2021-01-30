package application.domain.importer.parser;

import application.model.record.Record;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class SavakisParser implements IParsers {

    @Override
    public String getType() {
        return "savakis";
    }

    @Override
    public ParserResult parse(String invoiceContent) throws ParseException {
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
            } else if(
                    lines[i].contains("ΣΥΝΟΛΑ") ||
                            lines[i].contains("ΑΝΑΛΥΣΗ") ||
                            lines[i].contains("ΕΠΩΝΥΜΙΑ")
            ) {
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
                    record.setNumber(cols[offset+3].trim());
                } else if (cols.length == 11) {
                    record.setName(cols[1].trim());
                    record.setOrigin(cols[2].trim());
                    record.setNumber(cols[offset+3].trim());
                } else if(cols.length == 13) {
                    record.setName(cols[1] + " " + cols[2].trim());
                    record.setOrigin(cols[4].trim());
                    offset = 2;
                    record.setNumber(cols[offset+3].trim());
                } else if (cols.length == 10) {
                    // missing product number
                    record.setName(cols[1].trim());
                    record.setOrigin(cols[2].trim());
                    offset = -1;
                    record.setNumber("N/A");
                } else if(cols.length == 9) {
                    // missing origin and product number
                    record.setName(cols[1].trim());
                    record.setOrigin(" ");
                    offset = -2;
                    record.setNumber(" ");
                }

                record.setQuantity(cols[offset+6].trim());
                record.setPrice(cols[offset+7].trim());
                record.setTax(cols[offset+8].trim());
                record.setDiscount(cols[offset+10].trim());
                record.setMeasurement_unit("-");
                res.records.add(record);
            }
        }
        res.invoiceDate = dateTime;

        return res;
    }


}