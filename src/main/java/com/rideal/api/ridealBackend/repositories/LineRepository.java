package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Line;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "lines", path = "lines")
public interface LineRepository extends CrudRepository<Line, String> {
    Optional<Line> findById(String id);
}
