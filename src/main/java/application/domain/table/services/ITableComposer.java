package application.domain.table.services;


import application.domain.table.TableComposerDTO;

public interface ITableComposer {
    public TableComposerDTO createTable(String invoiceContent);
}
