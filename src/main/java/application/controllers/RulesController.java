package application.controllers;

import application.controllers.dtos.RulesDTO;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins="*")
public class RulesController {
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IRetailPricesRepository retailPricesRepository;

    @GetMapping("/getRules")
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

}
