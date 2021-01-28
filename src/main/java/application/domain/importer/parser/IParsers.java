package application.domain.importer.parser;

import java.text.ParseException;

public interface IParsers {
    public ParserResult parse(String doc) throws ParseException;
}
