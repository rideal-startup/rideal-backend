package com.rideal.api.ridealBackend.configuration;

import com.rideal.api.ridealBackend.models.City;
import com.rideal.api.ridealBackend.models.Company;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.CityRepository;
import com.rideal.api.ridealBackend.repositories.CompanyRepository;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final BasicUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;

    @Value("${spring.security.user.password}")
    private String adminPass;

    @Value("${rideal.passwords.db_manager}")
    private String dbManagerPass;

    @Value("${rideal.passwords.developer}")
    private String developerPass;

    public SecurityConfiguration(BasicUserDetailsService userDetailsService,
                                 UserRepository userRepository,
                                 CompanyRepository companyRepository, CityRepository cityRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.cityRepository = cityRepository;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return User.passwordEncoder;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode(adminPass)).roles("ADMIN")
                .and()
                .withUser("db_manager").password(passwordEncoder().encode(dbManagerPass)).roles("DB_MANAGER")
                .and()
                .withUser("developer").password(passwordEncoder().encode(developerPass)).roles("DEVELOPER");
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        final var city = cityRepository
                .findByName("Lleida")
                .orElseGet(() -> cityRepository.save(City.builder()
                        .country("Spain")
                        .name("Lleida")
                        .postalCode(12345)
                        .build()));

        if (!userRepository.existsByUsername("guillem")) {

            final var user = User.builder()
                    .username("guillem")
                    .name("Guillem")
                    .surname("Orellana Trullols")
                    .email("guillem@mail.com")
                    .city(city)
                    .password(passwordEncoder().encode("password"))
                    .build();
            userRepository.save(user);
        }

        if (companyRepository.findByUsername("ALSA").isEmpty()) {
            final var company = Company.companyBuilder()
                    .name("ALSA")
                    .email("ALSA@mail.com")
                    .city(city)
                    .password(passwordEncoder().encode("password"))
                    .build();
            companyRepository.save(company);
        }

        if (companyRepository.findByUsername("Moventis").isEmpty()) {
            final var company = Company.companyBuilder()
                    .name("Moventis")
                    .email("Moventis@mail.com")
                    .city(city)
                    .password(passwordEncoder().encode("password"))
                    .build();
            companyRepository.save(company);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .httpBasic().realmName("Rideal")
                .and()
                .cors()
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin();
    }
}