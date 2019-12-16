package com.rideal.api.ridealBackend.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import com.rideal.api.ridealBackend.services.FCMService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

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

    @Autowired
    private FCMService cloudMessaging;

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
            return ResponseEntity.ok().body(user
                    .getRequests()
                    .stream()
                    .map((userId) -> {
                        final var userOpt = userRepository.findById(userId);
                        return userOpt.get();
                    })
                    .collect(toList()));
        }
    }

    @GetMapping("/pendingApproval")
    public ResponseEntity<List<User>> getPendingApprovals() {
        final var userOptional =
                Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body(
                    userService
                            .getPendingApprovals(userOptional.get()));
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

    @PostMapping("/sendRequest/{id}")
    public ResponseEntity sendRequest(@PathVariable String id) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        final var otherUser = userRepository.findById(id);
        if (otherUser.isEmpty())
            return ResponseEntity.notFound().build();

        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var requests = otherUser.get().getRequests();

        // If user has already sent a request don't do nothing
        if (requests.contains(mySelf.getId()))
            return ResponseEntity.ok().build();

        requests.add(mySelf.getId());
        userRepository.save(otherUser.get());

        final var notification = new FCMService.RidealNotification(
                "Friend Request",
                mySelf.getUsername() + " sent you a friend request",
                Collections.singletonList(otherUser.get()));

        cloudMessaging.sendNotification(notification);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/acceptRequest/{id}")
    public ResponseEntity<User> acceptRequest(@PathVariable String id) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        final var otherUser = userRepository.findById(id);
        if (otherUser.isEmpty())
            return ResponseEntity.notFound().build();

        // Check if user is our requests
        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var requests = mySelf.getRequests();
        if (!requests.contains(otherUser.get().getId())) {
            return ResponseEntity.badRequest().build();
        }

        // Remove accepted request from request list
        final var afterAcceptRequest = mySelf
                .getRequests()
                .stream()
                .filter(u -> !u.equals(id))
                .collect(toList());
        mySelf.setRequests(afterAcceptRequest);

        // Add the accepted request as a new friend
        mySelf.getFriends().add(otherUser.get().getId());
        otherUser.get().getFriends().add(mySelf.getId());

        userRepository.save(mySelf);
        userRepository.save(otherUser.get());

        final var notification = new FCMService.RidealNotification(
                "Friend Request Accepted",
                mySelf.getUsername() + " accepted your  friend request",
                Collections.singletonList(otherUser.get()));

        cloudMessaging.sendNotification(notification);

        return ResponseEntity.ok(mySelf);
    }

    @PostMapping("/cancelRequest/{id}")
    public ResponseEntity cancelRequest(@PathVariable String id) {
        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Remove canceled request from requests list
        final var afterCancelRequest = mySelf
                .getRequests()
                .stream()
                .filter(u -> !u.equals(id))
                .collect(toList());
        mySelf.setRequests(afterCancelRequest);
        userRepository.save(mySelf);
        return ResponseEntity.ok(mySelf);
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

    /**
     * This method is thought to be used when looking for new users.
     * So users which already have a request or already are friends of the user
     * preforming the request, won't appear on the result
     */
    @GetMapping("/findByUsernameLike")
    public ResponseEntity<List<User>> findByUsernameLike(@RequestParam String username) {
        final var myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final var users = userRepository
                .findAll()
                .stream()
                .filter(u -> u.getUsername().toLowerCase().contains(username.toLowerCase()) &&
                                !myself.getFriends().contains(u.getId()) &&
                                !myself.getRequests().contains(u.getId()) &&
                                !u.getRequests().contains(myself.getId()) &&
                                !u.getId().equals(myself.getId()))
                .collect(toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable String id) {
        final var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();

        final var friends = userOpt.get()
                .getFriends()
                .stream().map(u -> userRepository.findById(u).get())
                .collect(toList());

        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<List<User>> getRequests(@PathVariable String id) {
        final var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();

        final var friends = userOpt.get()
                .getRequests()
                .stream().map(u -> userRepository.findById(u).get())
                .collect(toList());

        return ResponseEntity.ok(friends);
    }
}