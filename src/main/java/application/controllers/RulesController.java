package application.controllers;

import application.controllers.dtos.MappingsDialogDTO;
import application.controllers.dtos.RulesTableRowDTO;
import application.model.mappings.Mappings;
import application.model.mappings.services.IMappingsRepository;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import application.model.smast.Smast;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
public class RulesController {
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IRetailPricesRepository retailPricesRepository;
    @Autowired
    IMappingsRepository mappingsRepository;

    @GetMapping("/getRulesTable")
    public ResponseEntity getRules() {
        List<Rules> rules = rulesRepository.getRules();

        List<String> sCodes = rulesRepository.getScodes();
        RulesTableRowDTO[] rulesTableRowDTO = retailPricesRepository
                .getRetailPrices(sCodes)
                .stream()
                .map(
                        x -> {
                            Rules t = rules
                                    .stream()
                                    .filter(r -> r.getsCode().equals(x.getsCode()))
                                    .findFirst().get();
                            return new RulesTableRowDTO(
                                    x.getsCode(),
                                    t.getProfitPercentage(),
                                    t.getMinProfit(),
                                    x.getsName());
                        }
                ).toArray(RulesTableRowDTO[]::new);

        Arrays.sort(rulesTableRowDTO);
        return new ResponseEntity(rulesTableRowDTO, HttpStatus.OK);
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
        rulesRepository.deleteRule(rule);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getSName/{sCode}")
    public ResponseEntity getsName(@PathVariable("sCode") String sCode) {
        List<Smast> smastList =  retailPricesRepository.getRetailPrices(Arrays.asList(sCode));
        if( smastList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        String sname = smastList.get(0).getsName();
        RulesTableRowDTO dto = new RulesTableRowDTO(sCode,sname);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @GetMapping("/getMappingDialogData/{sCode}")
    public ResponseEntity getMappingDialogData(@PathVariable("sCode") String sCode) {
        MappingsDialogDTO response = new MappingsDialogDTO();
        List<Smast> smastList =  retailPricesRepository.getRetailPrices(Arrays.asList(sCode));
        if( smastList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        response.setsName(smastList.get(0).getsName());

        Rules rule = rulesRepository.getRule(sCode);
        if(rule != null) {
            response.setMinProfit(rule.getMinProfit());
            response.setProfitPercentage(rule.getProfitPercentage());
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("/addOrUpdateMappingDialogData")
    public ResponseEntity saveMappingDialogData(@RequestBody MappingsDialogDTO data) {


        Rules rule = new Rules();
        rule.setMinProfit(floatToBigDecimal(data.getMinProfit()));
        rule.setProfitPercentage(floatToBigDecimal(data.getProfitPercentage()));
        rule.setsCode(data.getsCode());

        HttpStatus response = addOrUpdateRule(rule).getStatusCode();
        if( response == HttpStatus.BAD_REQUEST){
            return new ResponseEntity( HttpStatus.BAD_REQUEST);
        }

        Mappings mapping = new Mappings();
        mapping.setpName(data.getpName());
        mapping.setsCode(data.getsCode());

        mappingsRepository.saveMapping(mapping);
        return  new ResponseEntity( HttpStatus.OK);
    }

    private BigDecimal floatToBigDecimal(float value) {
        return new BigDecimal(value);
    }
}
