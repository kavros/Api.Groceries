package application.domain.importer.parser.dateTime;

import java.sql.Timestamp;
import java.text.ParseException;

public interface IDateTimeParser {
    Timestamp getTimeStamp(String doc) throws ParseException;
}
