package application.domain.table;


import application.domain.current.prices.IRetailPricesRepository;
import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.repo.ISettingsRepository;

import java.util.ArrayList;
import java.util.List;

public class Table {

    public class Row {
        public String name;
        public double profitPercentage;
        public double invoicePrice;
        public double retailPrice;
        public double newPrice;
        public double profitInEuro;

        @Override
        public String toString() {
            return "Row{" +
                    "name='" + name + '\'' +
                    ", profitPercentage=" + profitPercentage +
                    ", invoicePrice=" + invoicePrice +
                    ", currentPrice=" + retailPrice +
                    ", newPrice=" + newPrice +
                    ", profitInEuro=" + profitInEuro +
                    '}';
        }

    }

    public List<Row> table;

    public Table(IInvoiceParser invoiceParser, ISettingsRepository settings, IRetailPricesRepository retailPricesRepo){

        table = new ArrayList<>();
        invoiceParser.getProducts().forEach( x ->{
            Row row =  new Row();
            row.name = x.name;
            row.invoicePrice = x.invoicePrice;
            row.profitPercentage = settings.getProfit(x.name);
            row.profitInEuro = settings.getMinProfit(x.name);
            row.retailPrice = retailPricesRepo.getRetailPrice(settings.getsCode(x.name));
            row.newPrice = (row.invoicePrice *1.13)*(row.profitPercentage +1);
            System.out.println(row);
            table.add(row);
        });
    }



}
