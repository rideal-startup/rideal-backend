package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "progresses")
@MappedSuperclass
public class Progress {

    @Id
    private String id;

    private Long start;
    private Integer progress;

    @DBRef
    User user;

    @DBRef
    Challenge challenge;

    Long points;
}
