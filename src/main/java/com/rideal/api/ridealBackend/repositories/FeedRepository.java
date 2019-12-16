package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "feeds", path = "feeds")
public interface FeedRepository extends CrudRepository<Feed, String> {
    Optional<Feed> findById(String s);
    List<Feed> findAll();
    Optional<List<Feed>> findByUserId(String userId);
}
