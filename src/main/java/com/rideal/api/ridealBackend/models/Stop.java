package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stop implements Serializable {
    @NotBlank
    private String name;

    @NotNull
    @Embedded
    @Valid
    private Coordinates location;

    @NotNull
    private Long waitTime;

    @NotNull
    private Integer order;
}

