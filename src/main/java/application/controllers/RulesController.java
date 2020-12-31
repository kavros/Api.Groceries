package application.controllers;

import application.controllers.dtos.RulesTableRowDTO;
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
        String sName = retailPricesRepository.getSName(newRule.getsCode());
        boolean isValid = rulesRepository.addOrUpdateRule(newRule);
        if(!isValid || sName == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteRule")
    public ResponseEntity deleteRule(@RequestBody Rules rule) {
        rulesRepository.deleteRule(rule);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getSName/{sCode}")
    public ResponseEntity getsName(@PathVariable("sCode") String sCode) {
        String sname = retailPricesRepository.getSName(sCode);
        if (sname == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        RulesTableRowDTO dto = new RulesTableRowDTO(sCode,sname);

        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
