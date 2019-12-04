package com.rideal.api.ridealBackend.streaming;

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

import java.util.Collections;
import java.util.List;


public class WebSocketSink extends RichSinkFunction<Message> {
    private transient StompSession session;

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
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
    }
}
