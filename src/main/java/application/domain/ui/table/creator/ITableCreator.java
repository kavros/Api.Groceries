package application.domain.ui.table.creator;


import application.model.table.Table;

public interface ITableCreator {
    public Table createTable(String invoiceContent);
}
