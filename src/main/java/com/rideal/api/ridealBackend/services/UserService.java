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

    public User persistUserChanges(User user) {
        return userRepository.save(user);
    }
}
