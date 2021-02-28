package application.controllers;

import application.controllers.dtos.RulesTableRowDTO;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import application.model.erp.services.IERPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*")
public class RulesController {
    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IERPRepository erpRepo;
    @Autowired
    IMappingsRepository mappingsRepository;

    @GetMapping("/getRulesTable")
    public ResponseEntity getRules() {
        List<Rule> rules = rulesRepository.getRules();
        List<Mapping> mappings = mappingsRepository.getMappings();

        List<String> sCodes = rulesRepository.getScodes();
        RulesTableRowDTO[] rulesTableRowDTO = erpRepo
                .getProducts(sCodes)
                .stream()
                .map(
                        x -> {
                            Rule t = rules
                                    .stream()
                                    .filter(r -> r.getsCode().equals(x.getsCode()))
                                    .findFirst().get();
                            List<String> pNames = mappings
                                    .stream()
                                    .filter(m -> m.getsCode().equals(x.getsCode()))
                                    .map(Mapping::getpName)
                                    .collect(Collectors.toList());
                            return new RulesTableRowDTO(
                                    x.getsCode(),
                                    t.getProfitPercentage(),
                                    t.getMinProfit(),
                                    x.getsName(),
                                    pNames
                            );
                        }
                ).toArray(RulesTableRowDTO[]::new);

        Arrays.sort(rulesTableRowDTO);
        return new ResponseEntity(rulesTableRowDTO, HttpStatus.OK);
    }

    @PostMapping("/addOrUpdateRule")
    public ResponseEntity addOrUpdateRule(@RequestBody Rule newRule) {
        String sName = erpRepo.getSName(newRule.getsCode());
        boolean isValid = rulesRepository.addOrUpdateRule(newRule);
        if(!isValid || sName == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteRule")
    public ResponseEntity deleteRule(@RequestBody Rule rule) {
        rulesRepository.deleteRule(rule);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getSName/{sCode}")
    public ResponseEntity getsName(@PathVariable("sCode") String sCode) {
        String sname = erpRepo.getSName(sCode);
        if (sname == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        RulesTableRowDTO dto = new RulesTableRowDTO(sCode,sname);

        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
