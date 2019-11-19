package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.City;
import com.rideal.api.ridealBackend.models.Company;
import com.rideal.api.ridealBackend.models.Line;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "lines", path = "lines")
public interface LineRepository extends CrudRepository<Line, String> {
    List<Line> findAll();
    Optional<Line> findById(String id);
    List<Line> findByCompany(Company company);
    Optional<List<Line>> findByName(String name);
    Optional<List<Line>> findByAvailable(Boolean available);
    Optional<List<Line>> findByOnFreeDays(Boolean onFreeDays);
    Optional<List<Line>> findByCity(City city);
}
