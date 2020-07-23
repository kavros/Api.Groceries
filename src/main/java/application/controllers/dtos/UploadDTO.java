package application.controllers.dtos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UploadDTO {
    public ArrayList<String> warnings  = new ArrayList<>();
    public ArrayList<String> errors    = new ArrayList<>();
    public List<UploadEntry> data = new ArrayList<>();
    public String invoiceDate;
}
