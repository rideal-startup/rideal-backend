package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@BasePathAwareController
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUseres(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "points") String sortBy,
            @RequestParam(defaultValue = "DESC") String order)
    {
        List<User> list = userService.getAllUsers(pageNo, pageSize, sortBy, order);

        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
