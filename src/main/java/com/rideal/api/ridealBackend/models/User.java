package com.rideal.api.ridealBackend.models;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Builder
@Document(collection = "users")
@Data
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotNull
    @JsonIdentityReference(alwaysAsId = true)
    private City city;
    @NotBlank
    private String email;
    @NotNull
    private Integer points;
}