package com.rideal.api.ridealBackend.configuration;

import com.rideal.api.ridealBackend.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.annotation.PostConstruct;

@Configuration
public class DatabaseConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(City.class);
        config.exposeIdsFor(Stop.class);
        config.exposeIdsFor(Company.class);
        config.exposeIdsFor(Challenge.class);
        config.exposeIdsFor(TransportationMode.class);
    }

    @Autowired
    private MappingMongoConverter mongoConverter;

    // Converts . into a mongo friendly char
    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        mongoConverter.setMapKeyDotReplacement("_");
    }
}