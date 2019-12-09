package com.rideal.api.ridealBackend.services;

import com.rideal.api.ridealBackend.models.Photo;
import com.rideal.api.ridealBackend.repositories.PhotoRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepo;

    public String addPhoto(String title, String userId, MultipartFile file) throws IOException {
        Photo photo = new Photo(title, userId);
        Optional<Photo> existentPhotos = this.findPhotoByUserId(userId);
        if (existentPhotos.isPresent()) {
            photo = existentPhotos.get();
        }
        photo.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        photo = photoRepo.save(photo);
        return photo.getId();
    }

    public Optional<Photo> getPhoto(String userId) {
        return this.findPhotoByUserId(userId);
    }

    private Optional<Photo> findPhotoByUserId(String userId) {
        List<Photo> photoList = photoRepo.findAll();
        List<Photo> photos = photoList.stream().filter(photo ->
                photo.getUserId().equals(userId)).collect(Collectors.toList());
        if (photos.size() >= 1) return Optional.of(photos.get(0));
        return Optional.empty();
    }
}