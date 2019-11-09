package com.rideal.api.ridealBackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideal.api.ridealBackend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class MyCoolConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
    }

    @Override
    public void configureConversionService(ConfigurableConversionService configurableConversionService) {

    }

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingRepositoryEventListener) {

    }

    @Override
    public void configureExceptionHandlerExceptionResolver(ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver) {

    }

    @Override
    public void configureHttpMessageConverters(List<HttpMessageConverter<?>> list) {

    }

    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {

    }

    @Autowired
    private MappingMongoConverter mongoConverter;

    // Converts . into a mongo friendly char
    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        mongoConverter.setMapKeyDotReplacement("_");
    }
}