package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Builder
@Document(collection = "challenges")
@Data
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private int goal;
    @NotNull
    private String unit;
    @NotNull
    private long timestamp;
    @NotNull
    private String city_id;
    @NotNull
    private int prize;
}