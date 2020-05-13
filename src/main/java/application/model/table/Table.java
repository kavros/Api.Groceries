package application.model.table;


import application.model.invoice.Invoice;
import application.model.kefalaio.data.reader.CurrentProductPrices;
import application.model.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class Table {

    public List<TableRow> table = new ArrayList<>();
    public Table(Invoice invoice, Settings settings, CurrentProductPrices prices){

        invoice.invoiceRows.forEach( x ->{
            TableRow row =  new TableRow();
            table.add(row);
        });
    }
}
