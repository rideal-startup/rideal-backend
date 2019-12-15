package com.rideal.api.ridealBackend.handlers;

import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RepositoryEventHandler
public class UsersEventHandler {

    final UserRepository userRepository;

    public UsersEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @HandleAfterCreate
    @Transactional
    public void handleUserPostCreate(User user){
        user.encodePassword();
        userRepository.save(user);
    }

    @HandleAfterSave
    @Transactional
    public void handleUserPostSave(User user){
        user.encodePassword();
        userRepository.save(user);
    }
}