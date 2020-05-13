package application.model.kefalaio.data.reader;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("currentPricesRepository")
public class CurrentPricesRepository implements ICurrentPricesRepository {

    @Override
    public CurrentProductPrices getCurrentPrices(List<String> kefalaioCode) {
        return null;
    }
}
