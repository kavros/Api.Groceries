package application.domain.upload.services;


import application.controllers.dtos.UploadDTO;

public interface ITableComposer {
    public UploadDTO createTable(String invoiceContent);
}
