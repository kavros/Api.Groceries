package application.domain.ui.table.creator;


import application.domain.table.Table;

public interface ITableCreator {
    public Table createTable(String invoiceContent);
}
