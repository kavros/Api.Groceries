package application.domain.importer.parser;

import application.model.records.Product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParserResult {
    public Timestamp invoiceDate;
    public List<String> warnings;
    public List<Product> products;

    public ParserResult(){
        warnings = new ArrayList<>();
        products = new ArrayList<>();
    }
}
