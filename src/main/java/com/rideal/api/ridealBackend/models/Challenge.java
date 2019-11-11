package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Builder
@Document(collection = "challenges")
@Data
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Integer goal;
    @NotBlank
    private String unit;
    @NotNull
    private Long timestamp;
    @DBRef
    @NotNull
    private City city;
    @NotNull
    private Prize prize;
}