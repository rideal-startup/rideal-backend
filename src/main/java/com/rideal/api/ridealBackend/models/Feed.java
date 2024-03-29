package com.rideal.api.ridealBackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "feeds")
public class Feed {
    @Id
    private String id;

    private String title;

    private String description;

    private String userId;

    private String username;

    public Feed(String title, String description, String username) {
        super();
        this.title = title;
        this.description = description;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Feed{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
