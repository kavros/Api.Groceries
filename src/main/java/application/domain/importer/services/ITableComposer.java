package application.domain.importer.services;


import application.controllers.stepper.dto.ImportDTO;

public interface ITableComposer {
    public ImportDTO createTable(String invoiceContent);
}
