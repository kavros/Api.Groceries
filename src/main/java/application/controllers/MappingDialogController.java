package application.controllers;

import application.controllers.dtos.MappingsDialogDTO;
import application.model.mappings.Mappings;
import application.model.mappings.services.IMappingsRepository;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import application.model.smast.services.IRetailPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@CrossOrigin(origins="*")
public class MappingDialogController {

    @Autowired
    IRulesRepository rulesRepository;
    @Autowired
    IRetailPricesRepository retailPricesRepository;
    @Autowired
    IMappingsRepository mappingsRepository;

    @GetMapping("/getMappingDialogData/{sCode}")
    public ResponseEntity getMappingDialogData(@PathVariable("sCode") String sCode) {
        MappingsDialogDTO response = new MappingsDialogDTO();
        String sName =  retailPricesRepository.getSName(sCode);
        if( sName == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        response.setsName(sName);

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

        boolean hasSucceed = rulesRepository.addOrUpdateRule(rule);
        if(!hasSucceed) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
