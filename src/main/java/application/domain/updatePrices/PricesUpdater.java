package application.domain.updatePrices;

import application.model.records.services.IRecordsRepository;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("pricesUpdater")
public class PricesUpdater implements IPricesUpdater {
    @Autowired
    IRetailPricesRepository retailPricesRepository;
    @Autowired
    IRecordsRepository recordsRepository;


    public void updatePrices(List<Map.Entry<String, Float>> data, String invoiceDate){
        recordsRepository.updatePrices(data,invoiceDate);

    }

}
