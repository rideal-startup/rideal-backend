package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Line;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "lines", path = "lines")
public interface LineRepository extends CrudRepository<Line, String> {
    Optional<Line> findById(String id);
    Optional<List<Line>> findByName(String name);
    Optional<List<Line>> findByCity(String city);
    Optional<List<Line>> findByAvailable();
    @Query("SELECT l FROM Line WHERE l.available = true and l.onFreeDays = true")
    Optional<List<Line>> findByIsFreeDay();
    @Query("SELECT * FROM Line WHERE l.available = true")
    Optional<List<Line>> findByIsNotFreeDay();
}
