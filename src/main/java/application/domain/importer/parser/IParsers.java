package application.domain.importer.parser;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

public interface IParsers {
    List<Product> parse(String doc);
    ParserType getType();
    Timestamp getTimeStamp(String doc) throws ParseException;

}
