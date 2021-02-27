package application.controllers;

import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="*")
public class MappingsController {

    @Autowired
    IMappingsRepository mappingsRepository;



    @DeleteMapping("/deleteMapping")
    public ResponseEntity deleteRule(@RequestBody Mapping mapping) {
        mappingsRepository.deleteMapping(mapping);
        return new ResponseEntity(HttpStatus.OK);
    }
}
