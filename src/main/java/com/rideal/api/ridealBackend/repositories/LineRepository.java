package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Line;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "lines", path = "lines")
public interface LineRepository extends CrudRepository<Line, String> {
    List<Line> findAll();
    Optional<Line> findById(String id);
    Optional<List<Line>> findByName(String name);
    Optional<List<Line>> findByAvailable(Boolean available);
    Optional<List<Line>> findByOnFreeDay(Boolean onFreeDay);


    @Query("SELECT l FROM Line WHERE l.city.name = :name")
    Optional<List<Line>> findLineWithCityName(@Param("name") String name);
}
