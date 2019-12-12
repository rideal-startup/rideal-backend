package com.rideal.api.ridealBackend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideal.api.ridealBackend.configuration.MapperConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import javax.json.JsonPatch;
import javax.json.JsonStructure;
import javax.json.JsonValue;

@Service
public class PatchService {

    private ApplicationContext context = new AnnotationConfigApplicationContext(MapperConfiguration.class);

    private ObjectMapper mapper = context.getBean("objectMapper", ObjectMapper.class);

    public <T> T patch(JsonPatch patch, T targetBean, Class<T> beanClass) {
        // Convert the Java bean to a JSON document
        JsonStructure target = mapper.convertValue(targetBean, JsonStructure.class);

        // Apply the JSON Patch to the JSON document
        JsonValue patched = patch.apply(target);

        // Convert the JSON document to a Java bean and return it
        return mapper.convertValue(patched, beanClass);
    }
}
