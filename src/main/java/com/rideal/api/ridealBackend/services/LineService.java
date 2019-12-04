package com.rideal.api.ridealBackend.services;

import com.rideal.api.ridealBackend.models.Stop;
import com.rideal.api.ridealBackend.repositories.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    @Autowired
    LineRepository lineRepository;

    public boolean isDuplicated(Stop stop, List<Stop> stops) {
        return !(stops.stream().filter(stop_inst ->
                stop_inst.getName().equals(stop.getName())).collect(Collectors.toList()).size() == 0);
    }

    public boolean isNameLike(String isA, String intoB) {
        return intoB.toLowerCase().contains(isA.toLowerCase());
    }

}
