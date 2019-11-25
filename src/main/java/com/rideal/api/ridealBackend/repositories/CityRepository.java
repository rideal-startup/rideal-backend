package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "cities", path = "cities")
public interface CityRepository extends CrudRepository<City, String> {
    Optional<City> findByName(String name);
    Optional<City> findByNameLike(String name);
    Optional<City> findByPostalCode(Integer postalCode);
    Optional<List<City>> findByCountry(String country);
}
