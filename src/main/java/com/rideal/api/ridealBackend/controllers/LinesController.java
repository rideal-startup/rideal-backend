package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.Company;
import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.LineRepository;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@BasePathAwareController
public class LinesController {

    private final LineRepository linesRepository;

    public LinesController(LineRepository linesRepository) {
        this.linesRepository = linesRepository;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
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
}
