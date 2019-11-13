package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "companies", path = "companies")
public interface CompanyRepository extends MongoRepository<Company, String> {
}
