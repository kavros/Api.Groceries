package application.domain.importer.price_calculator;

import application.model.mappings.Mappings;
import application.model.rule.Rule;
import org.springframework.stereotype.Component;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component("newPriceCalculator")
public class PriceCalculator implements IPriceCalculator {

    public float getNewPrice(
            String sName, float invoicePrice,
            List<Mappings> mappings, List<Rule> rules)
    {
        Rule rule = getRuleFor(sName,mappings,rules);
        float profitPercentage = rule.getProfitPercentage();
        float minimumProfit = rule.getMinProfit();

        float priceWithTax = (float) (invoicePrice * 1.13);
        float newPrice = priceWithTax * (profitPercentage + 1);

        if(newPrice-priceWithTax < minimumProfit )
            newPrice = priceWithTax + minimumProfit;

        return  round2Decimals(newPrice);
    }

    public float getHistoryCatalogPrice(
            String sCode,
            float invoicePrice,
            List<Rule> rules )
    {
        Optional<Float> percentage = rules
                .stream()
                .filter(x ->x.getsCode().equals(sCode))
                .map(Rule::getProfitPercentage)
                .findFirst();

        return round2Decimals((float)(invoicePrice*1.13)*(1.0f+percentage.get()));
    }

    private float round2Decimals(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String roundedNumber = df.format(number).replace(',','.');

        return Float.parseFloat(roundedNumber);
    }


    private Rule getRuleFor(String sName,
                            List<Mappings> mappings, List<Rule> rules)
    {
        Mappings mapping = mappings
                .stream()
                .filter(x -> x.getpName().equals(sName))
                .findFirst().get();

        Rule rule = rules
                .stream()
                .filter(r->r.getsCode().equals(mapping.getsCode()))
                .findFirst()
                .get();
        if(rules == null){
            throw new NoSuchElementException("Failed to retrieve rule for: "+sName);
        }
        return rule;
    }
}
