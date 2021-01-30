package application.domain.importer.parser;

import application.domain.importer.Product;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

public interface IParsers {
    public List<Product> parse(String doc);
    public String getType();
    public Timestamp getTimeStamp(String doc) throws ParseException;
}
