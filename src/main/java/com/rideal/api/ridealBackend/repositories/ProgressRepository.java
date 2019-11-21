package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Progress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "progresses", path = "progresses")
public interface ProgressRepository extends CrudRepository<Progress, String> {
    Optional<Progress> findByOrderByPoints();
    Optional<Progress> findByOrderByPointsAsc();
}
