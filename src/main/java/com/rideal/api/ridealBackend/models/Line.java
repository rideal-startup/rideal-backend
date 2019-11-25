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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Document(collection = "lines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Line implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @NotNull
    private Float length;

    @NotNull
    private Boolean available;

    @NotNull
    private Boolean onFreeDays;

    @NotNull
    @DBRef
    private City city;

    @DBRef
    private Company company;

    @Enumerated(EnumType.STRING)
    private TransportationMode transportationMode = TransportationMode.BUS;

    @Enumerated(EnumType.STRING)
    public LineType routeType = LineType.CIRCULAR;

    private List<Stop> stops = Collections.emptyList();

    public static enum LineType {
        UNIDIRECTIONAL,
        BIDIRECTIONAL,
        CIRCULAR
    }
}
