package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    Optional<User> findById(String id);
    List<User> findAll();
    Optional<User> findByUsername(String username);
    List<User> findByUsernameLike(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<List<User>> findAllByOrderByPointsDesc();
}
