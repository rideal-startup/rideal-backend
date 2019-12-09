package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {

}
