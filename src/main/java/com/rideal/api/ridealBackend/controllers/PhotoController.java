package com.rideal.api.ridealBackend.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import com.rideal.api.ridealBackend.models.Photo;
import com.rideal.api.ridealBackend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping("/api/users/{id}/profile/image")
    public ResponseEntity<byte[]> getPhoto(@PathVariable(value = "id") String userId) {
        Optional<Photo> photoOptional = photoService.getPhoto(userId);
        if (photoOptional.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        Photo photo = photoOptional.get();
        byte[] image = photo.getImage().getData();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @PostMapping("/api/users/{id}/profile/image")
    public ResponseEntity<String> addPhoto(@RequestParam("title") String title, @RequestParam("image") MultipartFile image,
                           @PathVariable(value = "id") String userId, Model model) throws IOException {
        String id = photoService.addPhoto(title, userId, image);
        if (id != null) {
            return new ResponseEntity<>("Successful operation", new HttpHeaders(), HttpStatus.OK);
        }
        return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
