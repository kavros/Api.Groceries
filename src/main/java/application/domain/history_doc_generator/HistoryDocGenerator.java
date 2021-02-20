package application.domain.history_doc_generator;

import application.domain.importer.price_calculator.IPriceCalculator;
import application.model.record.services.IRecordsRepository;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component("historyDocGenerator")
public class HistoryDocGenerator implements IHistoryDocGenerator {

    @Autowired
    IRetailPricesRepository retailPricesRepository;
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IRecordsRepository recordsRepo;
    @Autowired
    IPriceCalculator priceCalculator;

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
        List<String> allSCodes =
                rulesRepository.getRules()
                        .stream()
                        .map(Rule::getsCode)
                        .collect(Collectors.toList());

        Map<String, List<Float>> sCodeToInvoicePrices =
                recordsRepo.getLatestInvoicePrices(allSCodes);

         Map<String, List<Float>> sCodeToPercentagePrices
                 = getPercentagePrices(sCodeToInvoicePrices);
        List<Smast> products = retailPricesRepository
                .getRetailPrices(allSCodes);

        Map<String, List<Float>> sNameToPrices = new HashMap<>();
        for(String sCode : allSCodes){
            Optional<Smast> product = products
                    .stream()
                    .filter(x -> x.getsCode().equals(sCode))
                    .findFirst();

            if(product.isPresent()) {
                String sName = product.get().getsName();
                sNameToPrices.put(sName, sCodeToPercentagePrices.get(sCode));
            }else{
                System.err.println("Cannot find: "+sCode);
            }
        }
        return sNameToPrices;
    }

    private Map<String, List<Float>> getPercentagePrices(
            Map<String, List<Float>> sCodeToInvoicePrices )
    {
        List<Rule> rules = rulesRepository.getRules();
        Map<String, List<Float>> scodeToSetting = new HashMap<>();

        for(Map.Entry<String,List<Float>> scodeToInvoicePrice:
                sCodeToInvoicePrices.entrySet())
        {
            List<Float> prices = scodeToInvoicePrice.getValue();
            String sCode = scodeToInvoicePrice.getKey();

            for(int i=0; i < prices.size(); i++) {
                float catalogPrice = priceCalculator
                        .getHistoryCatalogPrice(sCode,prices.get(i),rules);
                prices.set (i, catalogPrice);
            }
            scodeToSetting.put(sCode, prices);
        }

        return scodeToSetting;
    }
}
