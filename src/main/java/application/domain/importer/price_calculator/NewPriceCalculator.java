package application.domain.importer.price_calculator;

import application.model.settings.Settings;
import application.model.settings.services.ISettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NoSuchElementException;

@Component("newPriceCalculator")
public class NewPriceCalculator implements INewPriceCalculator {

    @Autowired
    ISettingsRepository settingsRepo;

    public float getNewPrice(String sName, float invoicePrice){

        Settings setting = getSettingFor(sName);
        float profitPercentage = setting.getProfitPercentage();
        float minimumProfit = setting.getMinProfit();

        float priceWithTax = (float) (invoicePrice * 1.13);
        float newPrice = priceWithTax * (profitPercentage + 1);

        if(newPrice-priceWithTax < minimumProfit )
            newPrice = priceWithTax + minimumProfit;

        return  round2Decimals(newPrice);
    }

    private float round2Decimals(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String roundedNumber = df.format(number).replace(',','.');

        return Float.parseFloat(roundedNumber);
    }


    private Settings getSettingFor(String sName){
        Map<String,Settings> settingsMap = settingsRepo.getAllSettings();
        Settings setting = settingsMap.get(sName);
        if(setting == null){
            throw new NoSuchElementException("Failed to retrieve sCode for: "+sName);
        }
        return setting;
    }
}
