package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Document(collection = "cities")
@Data
@Entity
@AllArgsConstructor
public class TransportationMode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotBlank
    private String name;
}
