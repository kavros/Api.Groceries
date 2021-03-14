package application.controllers.settings;

import application.controllers.settings.dtos.MappingsDTO;
import application.model.erp.services.IERPRepository;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.services.IRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*")
public class MappingsController {

    @Autowired
    IMappingsRepository mappingsRepository;
    @Autowired
    IERPRepository erpRepo;
    @Autowired
    IRecordsRepository recordsRepo;

    @GetMapping("/getMappings")
    public ResponseEntity getMappings() {

        List<Mapping> mappings = mappingsRepository.getMappings();
        List<String> sCodes = mappings.stream()
                            .map(Mapping::getsCode)
                            .distinct().collect(Collectors.toList());

        MappingsDTO[] dto = erpRepo
                .getProducts(sCodes)
                .stream()
                .map( x -> {
                    MappingsDTO elem = new MappingsDTO();
                    elem.setsCode(x.getsCode());
                    List<String> pNames = mappings
                            .stream()
                            .filter(m -> m.getsCode().equals(x.getsCode()))
                            .map(Mapping::getpName)
                            .collect(Collectors.toList());
                    elem.setpNames(pNames);
                    elem.setsName(x.getsName());

                    return elem;
                }).toArray(MappingsDTO[]::new);;


        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @PostMapping("/updateMapping")
    public ResponseEntity updateMapping(@RequestBody Mapping mapping) {
        recordsRepo.updateScode(mapping.getpName(), mapping.getsCode());
        mappingsRepository.updateMapping(mapping);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteMapping")
    public ResponseEntity deleteMapping(@RequestBody Mapping mapping) {
        mappingsRepository.deleteMapping(mapping);
        return new ResponseEntity(HttpStatus.OK);
    }
}
