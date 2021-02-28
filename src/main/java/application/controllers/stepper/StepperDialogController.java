package application.controllers.stepper;

import application.controllers.stepper.dto.StepperDialogDTO;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import application.model.erp.services.IERPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@CrossOrigin(origins="*")
public class StepperDialogController {

    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IERPRepository erpRepo;
    @Autowired
    IMappingsRepository mappingsRepository;

    @GetMapping("/getStepperDialogData/{sCode}")
    public ResponseEntity getStepperDialogData(@PathVariable("sCode") String sCode) {
        StepperDialogDTO response = new StepperDialogDTO();
        String sName =  erpRepo.getSName(sCode);
        if( sName == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        response.setsName(sName);

        Rule rule = rulesRepository.getRule(sCode);
        if(rule != null) {
            response.setMinProfit(rule.getMinProfit());
            response.setProfitPercentage(rule.getProfitPercentage());
        }

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("/saveStepperDialogData")
    public ResponseEntity saveMappingDialogData(@RequestBody StepperDialogDTO data) {
        Rule rule = new Rule(data.getsCode(),
                data.getProfitPercentage(),
                data.getMinProfit());

        boolean hasSucceed = rulesRepository.addOrUpdateRule(rule);
        if(!hasSucceed) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Mapping mapping = new Mapping(data.getpName(), data.getsCode());

        mappingsRepository.saveMapping(mapping);
        return  new ResponseEntity( HttpStatus.OK);
    }

    private BigDecimal floatToBigDecimal(float value) {
        return new BigDecimal(value);
    }
}
