package com.rideal.api.ridealBackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.MappedSuperclass;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usage-logs")
@MappedSuperclass
public class UsageLogs {
    @DBRef
    protected User user;

    @DBRef
    protected Line line;

    protected Long useTime;

//    protected Long date;
}
