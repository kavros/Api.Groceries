package application.domain.history_doc_generator;

import application.model.records.services.IRecordsRepository;
import application.model.settings.services.ISettingsRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import application.model.settings.Settings;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph tmpParagraph = document.createParagraph();
        XWPFRun dateRun = tmpParagraph.createRun();
        dateRun.setText(getCurrentDate());
        dateRun.setBold(true);

        Map<String, List<Float>> sNameToPrices = getContent();
        XWPFTable table = document.createTable();
        String[] sNames = sNameToPrices
                .keySet()
                .toArray(new String[sNameToPrices.size()]);
        Arrays.sort(sNames);
        for (int i=0; i < sNames.length; i+=2) {

            XWPFTableRow row = table.createRow();

            setFirstCell(row, sNames[i]);
            addAndSetCell(row,getPricesFor(sNames[i],sNameToPrices));
            
            if(i+1 < sNames.length ) {
                addAndSetCell(row, sNames[i + 1]);
                addAndSetCell(row, getPricesFor(sNames[i+1],sNameToPrices));
            }
        }
        return  document;
    }

    private void setFirstCell(XWPFTableRow row, String text) {
        XWPFRun content = row.getCell(0).getParagraphs().get(0).createRun();
        setRunContent(content,text);
    }

    private void addAndSetCell(XWPFTableRow row, String text) {
        XWPFRun content = row.addNewTableCell().getParagraphs().get(0).createRun();
        setRunContent(content,text);
    }

    private void setRunContent(XWPFRun run, String text) {
        run.setText(text);
        run.setBold(true);
        run.setFontSize(12);
    }

    private String getCurrentDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
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
