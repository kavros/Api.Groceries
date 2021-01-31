package application.domain.importer.parser;

import application.domain.importer.Product;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

public interface IParsers {
    List<Product> parse(String doc);
    String getType();
    Timestamp getTimeStamp(String doc) throws ParseException;

}
