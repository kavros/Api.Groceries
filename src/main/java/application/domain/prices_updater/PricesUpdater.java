package application.domain.prices_updater;

import application.model.records.services.IRecordsRepository;
import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("pricesUpdater")
public class PricesUpdater implements IPricesUpdater {
    @Autowired
    IRetailPricesRepository retailPricesRepository;
    @Autowired
    IRecordsRepository recordsRepository;
    @Autowired
    ISettingsRepository settingsRepository;

    public void updatePrices(List<Map.Entry<String, Float>> pNameToPrice, String invoiceDate){
        recordsRepository.updatePrices(pNameToPrice, invoiceDate);
        List<Map.Entry<String, Float>> sCodesToPrices =  getSCodeToPriceMappings(pNameToPrice);

        retailPricesRepository.updatePrices(sCodesToPrices);
        System.out.println(sCodesToPrices);
    }

    public List<Map.Entry<String, Float>> getSCodeToPriceMappings(List<Map.Entry<String, Float>> data) {
        Map<String, Settings> settings = settingsRepository.getAllSettings();
        List<Map.Entry<String, Float>> sCodesToPrices = new ArrayList<>();
        for (Map.Entry<String, Float> entry : data) {
            Float newPrice      = entry.getValue();
            String productName  =  entry.getKey();
            String sCode = settings.get(productName).getsCode();
            Map.Entry<String, Float> elem = new AbstractMap.SimpleEntry(sCode, newPrice);
            sCodesToPrices.add(elem);
        }
        return sCodesToPrices;
    }
}
