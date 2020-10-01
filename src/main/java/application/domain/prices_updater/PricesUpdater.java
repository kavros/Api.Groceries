package application.domain.prices_updater;

import application.controllers.dtos.UpdatePricesDTO;
import application.model.records.services.IRecordsRepository;
import application.model.settings.services.ISettingsRepository;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
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

    public void updatePrices( UpdatePricesDTO dto){

        List<Map.Entry<String, BigDecimal>> sCodesToPrices =  new ArrayList<>();
        List<Map.Entry<String, BigDecimal>> pNameToPrice = new ArrayList<>();
        for(UpdatePricesDTO.Entry entry: dto.getProducts()){
            pNameToPrice.add(
                    new AbstractMap.SimpleEntry<>(
                            entry.getName(),entry.getNewPrice())
            );
            sCodesToPrices.add(
                    new AbstractMap.SimpleEntry<>(
                            entry.getsCode(),entry.getNewPrice())
            );
        }

        recordsRepository.updatePrices(pNameToPrice, dto.getInvoiceDate());
        retailPricesRepository.updatePrices(sCodesToPrices);
    }
}
