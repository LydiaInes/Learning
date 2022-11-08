package be.ifosup.learning.webservice.controller.formation;

import java.util.*;
import be.ifosup.learning.formations.entities.Formation;
import be.ifosup.learning.formations.service.FormationService;
import be.ifosup.learning.formations.repositories.FormationRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

@RestController
public class FormationController {
    @Autowired
    private FormationService formationservice;

    // RESTful API methods for Retrieval operations
    @GetMapping("/formations")
    public List<Formation> list() {
        return formationservice.listAll();
    }

    @GetMapping("/formations/{id}")
    public ResponseEntity<Formation> get(@PathVariable Long id) {
        try {
            Formation formation = formationservice.get(id);
            return new ResponseEntity<Formation>(formation, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<Formation>(HttpStatus.NOT_FOUND);
        }
    }

    // RESTful API method for Create operation

    @PostMapping("/formations")
    public void add(@RequestBody Formation formation) {
        formationservice.save(formation);
    }

    // RESTful API method for Update operation

    @PutMapping("/formations/{id}")
    public ResponseEntity<?> update(@RequestBody Formation formation, @PathVariable Long id) {
        try {
            Formation existFormation = formationservice.get(id);
            formationservice.save(formation);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // RESTful API method for Delete operation

    @DeleteMapping("/formations/{id}")
    public void delete(@PathVariable Long id) {
        formationservice.delete(id);
    }



}
