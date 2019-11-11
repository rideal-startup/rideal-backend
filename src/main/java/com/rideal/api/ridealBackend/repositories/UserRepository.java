package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findById(String id);
}
