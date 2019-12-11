package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.services.PatchService;
import com.rideal.api.ridealBackend.services.PhotoService;
import com.rideal.api.ridealBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonPatch;
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

    @Autowired
    private PatchService patchService;

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

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> pathUserData(@PathVariable String id, @RequestBody JsonPatch patchDocument) {
        Optional<User> optionalUser = userService.findUserById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        User userPatched = patchService.patch(patchDocument, optionalUser.get(), User.class);

        User resultUser = userService.persistUserChanges(userPatched);

        return new ResponseEntity<>(resultUser, new HttpHeaders(), HttpStatus.OK);
    }
}