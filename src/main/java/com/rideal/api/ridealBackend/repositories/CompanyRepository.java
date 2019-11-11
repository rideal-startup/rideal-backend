package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String> {
}
