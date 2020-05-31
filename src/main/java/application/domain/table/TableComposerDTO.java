package application.domain.table;

import java.util.ArrayList;
import java.util.List;

public class TableComposerDTO {
    public ArrayList<String> warnings  = new ArrayList<>();
    public ArrayList<String> errors    = new ArrayList<>();
    public List<Row> data = new ArrayList<>();

}
