package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "challenges")
public class Challenge implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Integer goal;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChallengeUnit unit;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChallengeDifficulty difficulty;

    @NotNull
    private Long duration;

    @DBRef(lazy = true)
    private City city;

    @NotNull
    @DBRef
    private Company company;

    @NotNull
    @Valid
    private Prize prize;

    public static enum ChallengeUnit {
        KM,
        MIN,
        TIMES
    }

    public static enum ChallengeDifficulty {
        GOLD,
        SILVER,
        BRONZE
    }
}