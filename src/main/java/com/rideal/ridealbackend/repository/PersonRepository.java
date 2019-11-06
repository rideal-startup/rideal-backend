package com.rideal.ridealbackend.repository;

import com.rideal.ridealbackend.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "persons", path = "persons")
public interface PersonRepository extends CrudRepository<Person, Serializable> {
    Optional<Person> findById(@RequestParam("id") String id);
    Optional<List<Person>> findByName(@RequestParam("name") String id);
    @RestResource(rel = "title-contains", path="title-contains")
    Page<Person> findByNameContaining(@Param("query") String query, Pageable page);
}
