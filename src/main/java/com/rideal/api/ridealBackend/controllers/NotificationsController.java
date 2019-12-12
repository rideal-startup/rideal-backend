package com.rideal.api.ridealBackend.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.rideal.api.ridealBackend.services.FCMService;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@BasePathAwareController
public class NotificationsController {
    private final FCMService fcm;

    public NotificationsController(FCMService fcm) {
        this.fcm = fcm;
    }

    @GetMapping("/notification")
    public ResponseEntity notifyFCMAll() throws ExecutionException, InterruptedException, FirebaseMessagingException {
        final var notification = new FCMService.RidealNotification(
                "test", "descriptoin", Collections.emptyList());
        this.fcm.sendNotification(notification);
        return ResponseEntity.ok().build();
    }

}
