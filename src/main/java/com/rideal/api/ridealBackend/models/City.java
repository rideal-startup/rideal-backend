package com.rideal.api.ridealBackend.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Index;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Document(collection = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotNull
    @Indexed(unique = true)
    private String name;
    @NotNull
    private Integer postalCod;
    @NotNull
    private String country;
}
