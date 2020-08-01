package application.domain.importer.services;


import application.controllers.dtos.ImportDTO;

public interface ITableComposer {
    public ImportDTO createTable(String invoiceContent);
}
