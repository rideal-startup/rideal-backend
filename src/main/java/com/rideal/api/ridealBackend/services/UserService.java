package com.rideal.api.ridealBackend.services;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(Integer pageNo, Integer pageSize, String sortBy, String order)
    {
        Sort.Direction direction = Sort.Direction.ASC;
        if (order.equals("DESC") || order.equals("desc")) direction = Sort.Direction.DESC;

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

        Page<User> pagedResult = userRepository.findAll(paging);

        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<User> findUserById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> getPendingApprovals(User me) {
        final var users = userRepository.findAll();

        return users.stream()
                .filter(u -> u.getRequests().contains(me.getId()))
                .collect(Collectors.toList());
    }

    public User persistUserChanges(User user) {
        return userRepository.save(user);
    }

    public Optional<User> appendFriends(User myself, String friendId) {
        Optional<User> optionalUser = userRepository.findById(friendId);
        if (optionalUser.isEmpty())
            return Optional.empty();
        List<String> myFriends = myself.getFriends();
        myFriends.add(optionalUser.get().getId());
        myself.setFriends(myFriends);
        userRepository.save(myself);
        return Optional.of(myself);
    }

    public Optional<User> deleteFriend(User myself, String friendId) {
        Optional<User> optionalUser = userRepository.findById(friendId);
        if (optionalUser.isEmpty()) return Optional.empty();
        List<String> myFriends = myself.getFriends().stream().filter(user ->
                !user.equals(friendId)).collect(Collectors.toList());
        myself.setFriends(myFriends);
        userRepository.save(myself);
        return Optional.of(myself);
    }
}
