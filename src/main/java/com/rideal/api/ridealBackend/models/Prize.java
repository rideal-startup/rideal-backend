package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Document(collection = "prizes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prize implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @NotNull
    private Integer amount;
    @NotNull
    private String name;
    @NotNull
    private String link;
}
