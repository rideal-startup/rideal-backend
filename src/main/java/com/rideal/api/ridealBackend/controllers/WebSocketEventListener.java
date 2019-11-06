package com.rideal.api.ridealBackend.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.HashMap;
import static java.lang.String.format;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private String user;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String username;

        if (event.getMessage().getHeaders().get("simpConnectMessage") instanceof GenericMessage) {
            GenericMessage<?> generic = (GenericMessage<?>) event.getMessage().getHeaders().get("simpConnectMessage");
            if (generic != null) {
                Object nativeHeaders = generic.getHeaders().get("nativeHeaders");
                Map<String, String> headers = parseJsonWithGson(mapToJson(nativeHeaders));
                username = headers.get("UserID");
                username = username.replace("[\"", "");
                this.user = username.replace("\"]", "");
                logger.info("Received a new web socket connection of user: " + this.user);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String,String> parseJsonWithGson(String jsonData) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(jsonData).getAsJsonObject();
        Map<String, Object> result = gson.fromJson(object, Map.class);
        Map<String,String> map = new HashMap<>();
        for (Map.Entry<String,Object> entry:result.entrySet()){
            map.put(entry.getKey(),mapToJson(entry.getValue()));
        }
        return map;
    }

    private static String mapToJson(Object o){
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        System.out.println("OK");
        String username = (headerAccessor.getSessionAttributes() == null) ?
            "Unknown" : headerAccessor.getSessionAttributes().get("username") == null ?
                "Unknown" : (String) headerAccessor.getSessionAttributes().get("username");

        String roomId = (headerAccessor.getSessionAttributes() == null) ?
            "Unknown" : headerAccessor.getSessionAttributes().get("room_id") == null ?
                "Unknown" : (String) headerAccessor.getSessionAttributes().get("room_id");

        if (headerAccessor.getSessionAttributes() != null) {
            logger.info("User Disconnected : " + username);

            messagingTemplate.convertAndSend(format("/channel/%s", roomId));
        }
    }
}
