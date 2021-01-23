package application.domain.importer.parser;

import application.model.record.Record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParserResult {
    public Timestamp invoiceDate;
    public List<String> warnings;
    public List<Record> records;

    public ParserResult(){
        warnings = new ArrayList<>();
        records = new ArrayList<>();
    }
}
