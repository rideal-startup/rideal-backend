package com.rideal.api.ridealBackend.controllers;

import com.google.gson.JsonObject;
import com.rideal.api.ridealBackend.models.City;
import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.repositories.CityRepository;
import com.rideal.api.ridealBackend.repositories.LineRepository;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@BasePathAwareController
public class CitiesController {

    private final CityRepository citiesRepository;
    private final LineRepository lineRepository;

    public CitiesController(CityRepository citiesRepository, LineRepository lineRepository) {
        this.citiesRepository = citiesRepository;
        this.lineRepository = lineRepository;
    }

    @GetMapping("/lines/findByCityName")
    @ResponseBody
    public ResponseEntity<List<Line>> getLinesByCityName(@RequestParam("name") String name) {
        Optional<City> optionalCity = citiesRepository.findByName(name);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            List<Line> allLines = lineRepository.findAll();
            if (allLines.size() <= 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Database has no Lines on it");
            }
            List<Line> linesFound = allLines.stream()
                    .filter(x -> x.getCity()
                            .getName()
                            .equals(name))
                    .collect(Collectors.toList());

            if (linesFound.size() >= 1) {
                return ResponseEntity.ok().body(linesFound);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City with name " + name + "has no Lines");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City with name " + name + " is not in the DB");
        }
    }
}
