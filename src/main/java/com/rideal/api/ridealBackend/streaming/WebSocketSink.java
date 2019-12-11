package com.rideal.api.ridealBackend.streaming;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class WebSocketSink extends RichSinkFunction<Message> {
    private transient StompSession session;
    private transient Firestore db;

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        }
    }

    private void initializeFirebase() throws IOException {
        final var serviceAccount =
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("rideal-startup-firebase-adminsdk-bdupn-1708a3a5b2.json");

        final var firestoreOptions = FirestoreOptions
                .getDefaultInstance().toBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        this.db = firestoreOptions.getService();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        initializeFirebase();

        List<Transport> transports = Collections.singletonList(
                new WebSocketTransport(new StandardWebSocketClient()));
        final var transport = new SockJsClient(transports);
        final var stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        var socketPort = System.getenv("PORT");
        if (socketPort == null || socketPort.isBlank())
            socketPort = "8080";

        session = stompClient.connect("ws://localhost:" + socketPort + "/socket/", new MyHandler()).get();
    }

    @Override
    public void invoke(Message value, Context context) {
        session.send("/chat", value);
        this.db
                .collection("lines-location")
                .document(value.getLineId())
                .set(value);
    }
}
