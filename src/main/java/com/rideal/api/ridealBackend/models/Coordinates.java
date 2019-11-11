package com.rideal.api.ridealBackend.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Coordinates {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
