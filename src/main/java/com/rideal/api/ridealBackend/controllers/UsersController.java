package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import com.rideal.api.ridealBackend.services.PatchService;
import com.rideal.api.ridealBackend.services.PhotoService;
import com.rideal.api.ridealBackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonPatch;
import java.util.List;
import java.util.Optional;

@BasePathAwareController
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PatchService patchService;

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

    @PutMapping("/addFriend")
    public ResponseEntity<User> addFriends(@RequestParam String id) {
        User myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> result = userService.appendFriends(myself, id);
        if (result.isEmpty()) return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>(result.get(), new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/deleteFriend")
    public ResponseEntity<User> deleteFriends(@RequestParam String id) {
        User myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> result = userService.deleteFriend(myself, id);
        if (result.isEmpty()) return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>(result.get(), new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User userUpdated) {
        User myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (myself.getId() != userUpdated.getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        myself.setUsername(userUpdated.getUsername());
        myself.setEmail(userUpdated.getEmail());
        userRepository.save(myself);
        return ResponseEntity.ok(myself);
    }
}