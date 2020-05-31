package application.domain.table;

import java.util.ArrayList;
import java.util.List;

public class TableComposerDTO {
    private ArrayList<String> warnings  = new ArrayList<>();
    private ArrayList<String> errors    = new ArrayList<>();
    public List<Row> data = new ArrayList<>();

}
