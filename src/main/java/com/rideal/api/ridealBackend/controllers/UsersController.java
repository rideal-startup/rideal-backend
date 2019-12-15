package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.City;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import com.rideal.api.ridealBackend.services.PatchService;
import com.rideal.api.ridealBackend.services.PhotoService;
import com.rideal.api.ridealBackend.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@BasePathAwareController
@RestController
@RequestMapping("/users")
public class UsersController {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        private String id;
        private String username;
        private String email;
        private String name;
        private String surname;
        private Integer points;
        private List<String> friends; // As references of type /users/{id}
        private List<String> requests;
        private City city;

        public UserDTO(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.name = user.getName();
            this.surname = user.getSurname();
            this.city = user.getCity();
            this.points = user.getPoints();
            this.friends = user.getFriends().stream().map((u) -> "/users/" + u.getId()).collect(toUnmodifiableList());
            this.requests = user.getRequests().stream().map((u) -> "/users/" + u.getId()).collect(toUnmodifiableList());
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PatchService patchService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "points") String sortBy,
            @RequestParam(defaultValue = "DESC") String order)
    {
        List<User> list = userService.getAllUsers(pageNo, pageSize, sortBy, order);
        return new ResponseEntity<>(list.stream().map(UserDTO::new).collect(toList()), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/followRequests")
    public ResponseEntity<List<UserDTO>> getFollowRequests() {
        final var userOptional =
                Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            final var user = userOptional.get();
            return ResponseEntity.ok().body(user.getRequests().stream().map(UserDTO::new).collect(toList()));
        }
    }

    @GetMapping("/pendingApproval")
    public ResponseEntity<List<UserDTO>> getPendingApprovals() {
        final var userOptional =
                Optional.ofNullable((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().body(
                    userService
                            .getPendingApprovals(userOptional.get())
                            .stream().map(UserDTO::new).collect(toList()));
        }
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<UserDTO> pathUserData(@PathVariable String id, @RequestBody JsonPatch patchDocument) {
        Optional<User> optionalUser = userService.findUserById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        User userPatched = patchService.patch(patchDocument, optionalUser.get(), User.class);

        User resultUser = userService.persistUserChanges(userPatched);

        return new ResponseEntity<>(new UserDTO(resultUser), new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/sendRequest/{id}")
    public ResponseEntity sendRequest(@PathVariable String id) {
        final var otherUser = userRepository.findById(id);
        if (otherUser.isEmpty())
            return ResponseEntity.notFound().build();

        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var requests = otherUser.get().getRequests();

        // If user has already sent a request don't do nothing
        if (requests.contains(mySelf))
            return ResponseEntity.ok().build();

        requests.add(mySelf);
        userRepository.save(otherUser.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/acceptRequest/{id}")
    public ResponseEntity<UserDTO> acceptRequest(@PathVariable String id) {
        final var otherUser = userRepository.findById(id);
        if (otherUser.isEmpty())
            return ResponseEntity.notFound().build();

        // Check if user is our requests
        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var requests = mySelf.getRequests();
        if (!requests.contains(otherUser.get())) {
            return ResponseEntity.badRequest().build();
        }

        // Remove accepted request from request list
        final var afterAcceptRequest = mySelf
                .getRequests()
                .stream()
                .filter(u -> !u.getId().equals(id))
                .collect(toList());
        mySelf.setRequests(afterAcceptRequest);

        // Add the accepted request as a new friend
        mySelf.getFriends().add(otherUser.get());
        otherUser.get().getFriends().add(mySelf);

        userRepository.save(mySelf);
        userRepository.save(otherUser.get());

        return ResponseEntity.ok(new UserDTO(mySelf));
    }

    @PostMapping("/cancelRequest/{id}")
    public ResponseEntity cancelRequest(@PathVariable String id) {
        final var mySelf = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Remove canceled request from requests list
        final var afterCancelRequest = mySelf
                .getRequests()
                .stream()
                .filter(u -> !u.getId().equals(id))
                .collect(toList());
        mySelf.setRequests(afterCancelRequest);
        userRepository.save(mySelf);
        return ResponseEntity.ok(new UserDTO(mySelf));
    }

    @PutMapping("/addFriend")
    public ResponseEntity<UserDTO> addFriends(@RequestParam String id) {
        User myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> result = userService.appendFriends(myself, id);
        if (result.isEmpty()) return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>(new UserDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/deleteFriend")
    public ResponseEntity<UserDTO> deleteFriends(@RequestParam String id) {
        User myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> result = userService.deleteFriend(myself, id);
        if (result.isEmpty()) return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>(new UserDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * This method is thought to be used when looking for new users.
     * So users which already have a request or already are friends of the user
     * preforming the request, won't appear on the result
     */
    @GetMapping("/findByUsernameLike")
    public ResponseEntity<List<UserDTO>> findByUsernameLike(@RequestParam String username) {
        final var myself = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final var users = userRepository
                .findAll()
                .stream()
                .filter(u -> u.getUsername().toLowerCase().contains(username.toLowerCase()) &&
                                !myself.getFriends().contains(u) &&
                                !myself.getRequests().contains(u) &&
                                !u.getRequests().contains(myself) &&
                                !u.getId().equals(myself.getId()))
                .map(UserDTO::new)
                .collect(toList());
        return ResponseEntity.ok(users);
    }
}