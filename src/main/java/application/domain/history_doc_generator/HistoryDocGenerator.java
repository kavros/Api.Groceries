package application.domain.history_doc_generator;

import application.model.records.services.IRecordsRepository;
import application.model.settings.services.ISettingsRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import application.model.settings.Settings;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
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

    public ByteArrayOutputStream GetDoc() {
        Map<String, List<Float>> sNameToPrices = getContent();
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        /*int i =0;
        for (String pName : sNameToPrices.keySet()){
            table.createRow();
            Cetable.getRow(i).addNewTableCell();
                .createCell().setText(pName);

            i++;
        }*/

        return null;
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
