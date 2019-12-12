package com.rideal.api.ridealBackend.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.rideal.api.ridealBackend.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RidealNotification {
        private String title;
        private String message;
        private List<User> to;
    }

    private Firestore db;

    @PostConstruct
    public void initialize() throws IOException {
        var serviceAccount =
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("rideal-startup-firebase-adminsdk-bdupn-1708a3a5b2.json");

        final var options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        serviceAccount =
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("rideal-startup-firebase-adminsdk-bdupn-1708a3a5b2.json");

        this.db = FirestoreOptions
                .getDefaultInstance().toBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
                .getService();
    }

    public void sendNotification(RidealNotification notification) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        if (notification.getTo().isEmpty()) {
            final var message = Message
                    .builder()
                    .setTopic("rideal")
                    .setNotification(new Notification(notification.getTitle(), notification.getMessage()))
                    .build();
            FirebaseMessaging.getInstance().sendAsync(message);
        } else {
            List<String> tokens = new ArrayList<>();
            for (final var user: notification.getTo()) {
                final var id = user.getId();
                final var token = (String) this.db
                        .collection("users")
                        .document(id).get().get().get("token");
                tokens.add(token);
            }

            final var message = MulticastMessage
                    .builder()
                    .addAllTokens(tokens)
                    .setNotification(new Notification(notification.getTitle(), notification.getMessage()))
                    .build();
            FirebaseMessaging.getInstance().sendMulticastAsync(message);
        }
    }
}

