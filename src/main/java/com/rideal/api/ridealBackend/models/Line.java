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
import java.sql.Timestamp;

@Builder
@Document(collection = "cities")
@Data
@Entity
@AllArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;
    @NotNull
    String name;
    @NotNull
    float length_km;
    @NotNull
    long journey_mean_time;
    @NotNull
    boolean available;
}
