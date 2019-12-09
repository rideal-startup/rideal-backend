package com.rideal.api.ridealBackend.models;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "photos")
public class Photo {
    @Id
    private String id;

    private String title;

    private String userId;

    private Binary image;

    public Photo(String title, String userId) {
        super();
        this.title = title;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", image=" + image +
                '}';
    }
}
