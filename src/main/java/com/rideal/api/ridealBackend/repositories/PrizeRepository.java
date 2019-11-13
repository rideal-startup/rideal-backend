package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Prize;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "prizes", path = "prizes")
public interface PrizeRepository extends CrudRepository<Prize, String> {
    Optional<Prize> findById(String id);
}
