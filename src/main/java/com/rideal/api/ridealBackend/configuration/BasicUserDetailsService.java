package com.rideal.api.ridealBackend.configuration;

import com.rideal.api.ridealBackend.repositories.CompanyRepository;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BasicUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public BasicUserDetailsService(UserRepository userRepository,
                                   CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseGet(() -> companyRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Incorrect email")));
    }
}