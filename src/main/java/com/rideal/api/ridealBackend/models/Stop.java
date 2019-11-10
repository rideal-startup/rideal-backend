package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Document(collection = "stops")
@Data
@Entity
@AllArgsConstructor
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank
    private String name;
    @NotNull
    private GeoJson location;
    @NotNull
    private Long avgWaitTime;
    @NotNull
    private Boolean available;
}

