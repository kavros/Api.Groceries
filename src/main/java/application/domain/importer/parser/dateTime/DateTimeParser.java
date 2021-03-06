package application.domain.importer.parser.dateTime;

import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DateTimeParser implements IDateTimeParser {

    @Override
    public Timestamp getTimeStamp(String doc) throws ParseException {

        String dateRegex = "[0-2]?[0-9]/[0-2]?[0-9]/[0-9]?[0-9]?[0-9]?[0-9]";
        String timeRegex = "([0-1][0-9]|[2][0-3]):[0-5][0-9]";

        Matcher dateMatcher = Pattern.compile(dateRegex).matcher(doc);
        Matcher timeMatcher = Pattern.compile(timeRegex).matcher(doc);
        if(dateMatcher.find() && timeMatcher.find())
        {
            String dateTime =  dateMatcher.group(0) + " " +timeMatcher.group(0);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
            Date date = formatter.parse(dateTime);
            return new java.sql.Timestamp(date.getTime());
        }

        return null;
    }
}
