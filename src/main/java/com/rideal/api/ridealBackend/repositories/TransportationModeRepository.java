package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.TransportationMode;
import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "transportationModes", path = "transportationModes")
public interface TransportationModeRepository extends CrudRepository<TransportationMode, String> {
    Optional<TransportationMode> findById(String id);
}
