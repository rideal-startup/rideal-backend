package com.rideal.ridealbackend.controller;

import com.rideal.ridealbackend.domain.Person;
import com.rideal.ridealbackend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

@BasePathAwareController
public class PersonController {
    private PersonRepository personsRepository;

    @Autowired
    public PersonController(PersonRepository personsRepository) {
        this.personsRepository = personsRepository;
    }

    @GetMapping(value = "/FileByName")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public ResponseEntity workspaceAskToWrite(
            @RequestParam String name,
            PersistentEntityResourceAssembler resourceAssembler) {

        if (name.isEmpty())
            return new ResponseEntity<>(
                    "Name param was not provided. Tip: {URL}/FileByName?name=PersonName",
                    HttpStatus.BAD_REQUEST);

        Optional<List<Person>> personsOptional = personsRepository.findByName(name);
        return personsOptional.map(people -> new ResponseEntity<>(people.toString(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("There ara no Persons with name: \"" + name + "\" in our " +
                        "database", HttpStatus.NOT_FOUND));

    }
}
