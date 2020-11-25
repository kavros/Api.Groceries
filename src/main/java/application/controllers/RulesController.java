package application.controllers;

import application.controllers.dtos.RulesDTO;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
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
                .values()
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
    public ResponseEntity addRule(@RequestBody Rules newRule) {
        //TODO: validate sCode
        if(isRuleValid(newRule))
            new ResponseEntity(HttpStatus.BAD_REQUEST);

        rulesRepository.addOrUpdateRule(newRule);
        return new ResponseEntity(HttpStatus.OK);
    }

    private boolean isRuleValid(Rules newRule){
        boolean isMinProfitValid = newRule.getMinProfit() > 0;
        boolean isPercentageValid = newRule.getProfitPercentage() > 0
                && newRule.getProfitPercentage() < 1;
        return isMinProfitValid && isPercentageValid;

    }

    @DeleteMapping("/addOrUpdateRule")
    public ResponseEntity deleteRule(@RequestBody Rules rule) {
        rulesRepository.deleteRule(rule);
        return new ResponseEntity(HttpStatus.OK);
    }
}
