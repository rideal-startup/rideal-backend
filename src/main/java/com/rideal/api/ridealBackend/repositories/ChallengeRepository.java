package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Challenge;
import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "challenges", path = "challenges")
public interface ChallengeRepository extends CrudRepository<Challenge, String> {
    Optional<Challenge> findById(String id);
}
