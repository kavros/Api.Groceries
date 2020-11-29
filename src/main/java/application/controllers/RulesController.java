package application.controllers;

import application.controllers.dtos.RulesDTO;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
public class RulesController {
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IRetailPricesRepository retailPricesRepository;

    @GetMapping("/getRulesTable")
    public ResponseEntity getRules() {
        List<Rules> rules = rulesRepository.getRules();

        List<String> sCodes = rulesRepository.getScodes();
        RulesDTO[] rulesDTO = retailPricesRepository
                .getRetailPrices(sCodes)
                .stream()
                .map(
                        x -> {
                            Rules t = rules
                                    .stream()
                                    .filter(r -> r.getsCode().equals(x.getsCode()))
                                    .findFirst().get();
                            return new RulesDTO(
                                    x.getsCode(),
                                    t.getProfitPercentage(),
                                    t.getMinProfit(),
                                    x.getsName());
                        }
                ).toArray(RulesDTO[]::new);

        Arrays.sort(rulesDTO);
        return new ResponseEntity(rulesDTO, HttpStatus.OK);
    }

    @PostMapping("/addOrUpdateRule")
    public ResponseEntity addOrUpdateRule(@RequestBody Rules newRule) {
        String sName = getSName(newRule.getsCode());
        if(!isRuleValid(newRule) || sName == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        rulesRepository.addOrUpdateRule(newRule);
        return new ResponseEntity(HttpStatus.OK);
    }

    private String getSName(String sCode) {
         List<Smast> smastList =  retailPricesRepository
                .getRetailPrices(Arrays.asList(sCode));
         return smastList.isEmpty()? null: smastList.get(0).getsName();
    }

    private boolean isRuleValid(Rules newRule){
        boolean isMinProfitValid = newRule.getMinProfit() > 0;
        boolean isPercentageValid = newRule.getProfitPercentage() > 0
                && newRule.getProfitPercentage() < 1;

        return isMinProfitValid && isPercentageValid;

    }

    @DeleteMapping("/deleteRule")
    public ResponseEntity deleteRule(@RequestBody Rules rule) {
        //TODO: don't allow to delete something that exists in records
        rulesRepository.deleteRule(rule);
        return new ResponseEntity(HttpStatus.OK);
    }
}
