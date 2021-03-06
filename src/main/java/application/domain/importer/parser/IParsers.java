package application.domain.importer.parser;

import java.util.List;

public interface IParsers {
    List<Product> parse(String doc);
    ParserType getType();
}
