package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "cities", path = "cities")
public interface CityRepository extends CrudRepository<City, String> {
    Optional<City> findById(String id);
}
