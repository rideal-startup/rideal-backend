package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Stop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "stops", path = "stops")
public interface StopRepository extends CrudRepository<Stop, String> {
    Optional<Stop> findById(@RequestParam("id") String id);
}
