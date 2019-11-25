package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prize {
    @NotNull
    private String name;

    @NotNull
    private String link;
}
