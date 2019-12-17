package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.Challenge;
import com.rideal.api.ridealBackend.models.Company;
import com.rideal.api.ridealBackend.models.Progress;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.ChallengeRepository;
import com.rideal.api.ridealBackend.repositories.ProgressRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@BasePathAwareController
public class ChallengesController {

    private final ChallengeRepository challengeRepository;
    private final ProgressRepository progressRepository;

    public ChallengesController(ChallengeRepository challengeRepository, ProgressRepository progressRepository) {
        this.challengeRepository = challengeRepository;
        this.progressRepository = progressRepository;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping("/challenges/{id}/enroll")
    @ResponseBody
    public ResponseEntity enrollToChallenge(@PathVariable String id) {
        final var userOptional = Optional.ofNullable(
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );

        if (userOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        final var challengeOptional = this.challengeRepository.findById(id);
        if (challengeOptional.isEmpty())
            return ResponseEntity.notFound().build();

        final var progress = Progress.builder()
                .progress(0)
                .user(userOptional.get())
                .start(new Date().getTime())
                .challenge(challengeOptional.get())
                .points(0L)
                .build();

        progressRepository.save(progress);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/challenges/running")
    @ResponseBody
    public ResponseEntity<List<ChallengeDTO>> challengeRunning() {
        final var userOptional = Optional.ofNullable(
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );

        if (userOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        final var progresses = this.progressRepository.findAll();

        final var userProgresses = progresses
                .stream()
                .filter(progress -> progress.getUser().equals(userOptional.get()))
                .collect(Collectors.toList());

        final var inProgressChallenges = userProgresses
                .stream()
                .map(Progress::getChallenge)
                .collect(Collectors.toList());

        final var challenges = this.challengeRepository.findAll()
                .stream()
                .filter(challenge -> !inProgressChallenges.contains(challenge))
                .collect(Collectors.toList());

        List<ChallengeDTO> result = challenges
                .stream()
                .map(ChallengeDTO::new)
                .collect(Collectors.toList());

        result.addAll(userProgresses
                        .stream()
                        .map(ChallengeDTO::new)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/challenges")
    @ResponseBody
    public ResponseEntity<List<Challenge>> getChallenges() {
        final var authCtxt = SecurityContextHolder
                .getContext()
                .getAuthentication();

        final var roles = authCtxt.getAuthorities();
        final var isAnonUser = roles
                .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

        if (isAnonUser)
            return ResponseEntity.ok(toList(challengeRepository.findAll()));

        final var user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        final var isCompany = roles
                .contains(new SimpleGrantedAuthority("ROLE_COMPANY"));

        // If the requesting user is a company return only their lines
        final var result = isCompany ?
                challengeRepository.findByCompany((Company) user) :
                toList(toList(challengeRepository.findAll()));

        return ResponseEntity.ok(result);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChallengeDTO {
        private Challenge challenge;
        private Integer progress;
        private Long start;
        private Long points;

        public ChallengeDTO(Progress p) {
            this.challenge = p.getChallenge();
            this.progress = p.getProgress();
            this.points = p.getPoints();
            this.start = p.getStart();
        }

        public ChallengeDTO(Challenge c) {
            this.challenge = c;
            this.progress = null;
            this.points = null;
            this.start = null;
        }
    }
}