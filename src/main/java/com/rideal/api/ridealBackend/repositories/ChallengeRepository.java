package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Challenge;
import com.rideal.api.ridealBackend.models.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "challenges", path = "challenges")
public interface ChallengeRepository extends CrudRepository<Challenge, String> {
    Optional<Challenge> findById(String id);
    List<Challenge> findByCompany(Company company);
    List<Challenge> findAll();
}
