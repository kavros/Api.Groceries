package application.domain.history_doc_generator;

import application.model.records.services.IRecordsRepository;
import application.model.settings.services.ISettingsRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import application.model.settings.Settings;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("historyDocGenerator")
public class HistoryDocGenerator implements IHistoryDocGenerator {
    @Autowired
    ISettingsRepository settingsRepo;
    @Autowired
    IRecordsRepository recordsRepo;

    public byte[] getDoc() throws IOException {
        XWPFDocument document = getDocument();
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        document.write(out);
        out.close();
        return out.toByteArray();
    }

    private XWPFDocument getDocument()  {
        Map<String, List<Float>> sNameToPrices = getContent();
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        String[] sNames = sNameToPrices
                .keySet()
                .toArray(new String[sNameToPrices.size()]);

        for (int i=0; i < sNames.length; i+=2) {

            XWPFTableRow row = table.createRow();
            row.addNewTableCell();
            row.addNewTableCell();
            row.addNewTableCell();
            row.getCell(0).setText(sNames[i]);
            row.getCell(1).setText(getPricesFor(sNames[i],sNameToPrices));

            if(i+1 < sNames.length ) {
                row.getCell(2).setText(sNames[i + 1]);
                row.getCell(3).setText(getPricesFor(sNames[i+1],sNameToPrices));
            }
        }
        return  document;
    }

    private String getPricesFor(String sName, Map<String, List<Float>> sNameToPrices ){
        String s = "";
        List<Float> prices = sNameToPrices.get(sName);

        for(Float price : prices) {
            if(s.isEmpty()) {
                s = price.toString();
            } else {
                s += ", " + price.toString();
            }
        }
        return s;
    }

    private  Map<String, List<Float>> getContent() {
        Map<String, Settings> allSettings = settingsRepo.getAllSettings();
        List<String> allDistinctSCodes =
                allSettings
                        .values()
                        .stream()
                        .map(Settings::getsCode)
                        .distinct()
                        .collect(Collectors.toList());

        Map<String, List<Float>> sCodeToPrices =
                recordsRepo.getLatestPrices(allDistinctSCodes);
        Map<String, List<Float>> sNameToPrices = new HashMap<>();
        for(String sCode : allDistinctSCodes){
            String sName = allSettings
                    .values()
                    .stream()
                    .filter(x -> x.getsCode().equals(sCode))
                    .findFirst().get().getsName();

            sNameToPrices.put(sName, sCodeToPrices.get(sCode));
        }
        return sNameToPrices;
    }

}
