package com.rideal.api.ridealBackend.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;

@Builder
@Document(collection = "cities")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;
    @NotNull
    String name;
    @NotNull
    Integer postalCod;
    @NotNull
    String country;
}
