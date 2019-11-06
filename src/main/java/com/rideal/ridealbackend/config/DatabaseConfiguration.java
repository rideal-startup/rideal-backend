package com.rideal.ridealbackend.config;

import com.rideal.ridealbackend.domain.Person;
import com.rideal.ridealbackend.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

import javax.annotation.PostConstruct;

@Configuration
public class DatabaseConfiguration implements RepositoryRestConfigurer, ApplicationRunner {

    private final PersonRepository personRepository;

    @Autowired
    DatabaseConfiguration(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Person.class);
    }

    @Autowired
    private MappingMongoConverter mongoConverter;

    // Converts '.'s into a Mongo friendly char
    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        mongoConverter.setMapKeyDotReplacement("_");
    }

    public void run(ApplicationArguments applicationArguments) {
        Person admin = new Person("ADMIN");
        personRepository.save(admin);
    }
}