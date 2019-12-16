package com.rideal.api.ridealBackend.controllers;

import com.rideal.api.ridealBackend.models.Feed;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.FeedRepository;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@BasePathAwareController
@RestController
@RequestMapping("/feeds")
public class FeedController {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getMyFriendsFeedById")
    public ResponseEntity<List<Feed>> getFollowRequests(@RequestParam String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), new HttpHeaders(), HttpStatus.OK);
        }
        List<String> friendsIds = userOptional.get().getFriends();
        List<Feed> feedsList = feedRepository.findAll();
        List<Feed> myFeed = feedsList.stream().filter(feed ->
                friendsIds.contains(feed.getUserId())).collect(Collectors.toList());
        return new ResponseEntity<>(myFeed, new HttpHeaders(), HttpStatus.OK);
    }

}
