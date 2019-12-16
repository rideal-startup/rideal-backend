package com.rideal.api.ridealBackend.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rideal.api.ridealBackend.models.Company;
import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.models.Stop;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.LineRepository;
import com.rideal.api.ridealBackend.services.FCMService;
import com.rideal.api.ridealBackend.services.LineService;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@BasePathAwareController
public class LinesController {

    private final LineService lineService;
    private final LineRepository linesRepository;
    private final FCMService cloudMessaging;

    public LinesController(LineService lineService, LineRepository linesRepository, FCMService cloudMessaging) {
        this.lineService = lineService;
        this.linesRepository = linesRepository;
        this.cloudMessaging = cloudMessaging;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping("/lines/{id}/setNotAvailable")
    @ResponseBody
    public ResponseEntity setLineNotAvailable(@PathVariable String id) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        final var lineOptional = this.linesRepository.findById(id);
        if (lineOptional.isEmpty())
            return ResponseEntity.notFound().build();

        final var notification = new FCMService.RidealNotification(
                "Line not available",
                "Line " + lineOptional.get().getName() + " is not available due to maintenance",
                Collections.emptyList()
        );
        cloudMessaging.sendNotification(notification);

        lineOptional.get().setAvailable(false);
        linesRepository.save(lineOptional.get());
        return ResponseEntity.ok().build();

    }
    @GetMapping("/lines")
    @ResponseBody
    public ResponseEntity<List<Line>> getLines() {
        final var authCtxt = SecurityContextHolder
                .getContext()
                .getAuthentication();

        final var roles = authCtxt.getAuthorities();
        final var isAnonUser = roles
                .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

        if (isAnonUser)
            return ResponseEntity.ok(toList(linesRepository.findAll()));

        final var user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        final var isCompany = roles
                .contains(new SimpleGrantedAuthority("ROLE_COMPANY"));

        // If the requesting user is a company return only their lines
        final var result = isCompany ?
                linesRepository.findByCompany((Company) user) :
                toList(toList(linesRepository.findAll()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/lines/containStop")
    @ResponseBody
    List<Line> linesContainingStop(@RequestParam String stopName) {
        final var lines = this.linesRepository.findAll();
        return lines
                .stream()
                .filter(l -> {
                    final var stopNames = l.getStops().stream().map(Stop::getName);
                    return stopNames.collect(Collectors.toList()).contains(stopName);
                })
                .sorted(Comparator.comparing(Line::getName))
                .collect(Collectors.toList());
    }

    @GetMapping("/stops/findStopsByNameLike")
    @ResponseBody
    public ResponseEntity<List<Stop>> findStopsByNameLike(@RequestParam String name) {
        List<Line> lines = linesRepository.findAll();
        List<Stop> stops = new ArrayList<>();

        for (Line line : lines) {
            for (Stop stop : line.getStops()) {
                if (!this.lineService.isDuplicated(stop, stops) && this.lineService.isNameLike(name, stop.getName())) {
                    stops.add(stop);
                }
            }
        }
        return new ResponseEntity<>(stops, new HttpHeaders(), HttpStatus.OK);
    }
}
