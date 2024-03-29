package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanyRepository extends CrudRepository<Company, String> {
    Boolean existsByEmail(String username);
    Optional<Company> findByUsername(String username);
    Optional<Company> findByEmail(String email);
}
