package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company, String> {
}
