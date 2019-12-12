package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.services.PhotoService;
import com.rideal.api.ridealBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@BasePathAwareController
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "points") String sortBy,
            @RequestParam(defaultValue = "DESC") String order)
    {
        List<User> list = userService.getAllUsers(pageNo, pageSize, sortBy, order);
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/followRequests")
    public ResponseEntity<List<User>> getFollowRequests() {
        final var userOptional =
                Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            final var user = userOptional.get();
            return ResponseEntity.ok().body(user.getRequests());
        }
    }

    @GetMapping("/pendingApproval")
    public ResponseEntity<List<User>> getPendingApprovals() {
        final var userOptional =
                Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body(userService.getPendingApprovals(userOptional.get()));
        }
    }
}