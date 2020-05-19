package application.domain.table;


import application.domain.current.prices.ICurrentPricesRepository;
import application.domain.invoice.parser.IInvoiceParser;
import application.domain.settings.repo.ISettingsRepository;

import java.util.ArrayList;
import java.util.List;

public class Table {

    public class Row {
        public String name;
        public double profitPercentage;
        public double invoicePrice;
        public double currentPrice;
        public double newPrice;
        public double profitInEuro;

        @Override
        public String toString() {
            return "Row{" +
                    "name='" + name + '\'' +
                    ", profitPercentage=" + profitPercentage +
                    ", invoicePrice=" + invoicePrice +
                    ", currentPrice=" + currentPrice +
                    ", newPrice=" + newPrice +
                    ", profitInEuro=" + profitInEuro +
                    '}';
        }

    }

    public List<Row> table;

    public Table(IInvoiceParser invoiceParser, ISettingsRepository settings, ICurrentPricesRepository currentPricesRepo){

        table = new ArrayList<>();
        invoiceParser.getProducts().forEach( x ->{
            Row row =  new Row();
            row.name = x.name;
            row.invoicePrice = x.invoicePrice;
            row.profitPercentage = settings.getProfit(x.name);
            row.profitInEuro = settings.getMinProfit(x.name);
            row.currentPrice = currentPricesRepo.getCurrentPrice(settings.getsCode(x.name));
            row.newPrice = (row.invoicePrice *1.13)*(row.profitPercentage +1);
            System.out.println(row);
            table.add(row);
        });
    }



}
