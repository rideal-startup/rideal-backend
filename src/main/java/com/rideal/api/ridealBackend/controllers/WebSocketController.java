package com.rideal.api.ridealBackend.controllers;

import com.google.gson.Gson;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate template;
    private Gson gson = new Gson();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/send/message")
    public void onReceivedMessage(SimpMessageHeaderAccessor accessor, String message) {
        Object nativeHeaders = accessor.getMessageHeaders().get("nativeHeaders");
        System.out.println(nativeHeaders);
        String[] fields = (nativeHeaders != null) ? nativeHeaders.toString().split("(?! )([, {}*]+)") : new String[] {}; // Regexp: Match spaces only if preceded by comma
        Map<String, String> rcvHeaders;
        rcvHeaders = this.parseHeaders(fields);

        logger.info("Received message from: " + rcvHeaders.get("UserID"));
        Map<String, Object> sndHeaders = buildSendHeaders(rcvHeaders);

        logger.info("Message to room: " + rcvHeaders.get("room_id") + " into file: " + rcvHeaders.get("file_id"));
        this.template.convertAndSend( format("/chat/%s", rcvHeaders.get("room_id")), message, sndHeaders);
    }

    private Map<String, Object> buildSendHeaders(Map<String, String> rcvHeaders) {
        Map<String, Object> sndHeaders = new HashMap<>();
        sndHeaders.put("UserID", rcvHeaders.get("UserID"));
        sndHeaders.put("room_id", rcvHeaders.get("room_id"));
        sndHeaders.put("file_id", rcvHeaders.get("file_id"));
        sndHeaders.put("action", rcvHeaders.get("action"));
        return sndHeaders;
    }

    @MessageMapping("/send/request")
    public void onReceivedRequest(SimpMessageHeaderAccessor accessor, String body) {
        System.out.println("Message received!" + body);
        Object nativeHeaders = accessor.getMessageHeaders().get("nativeHeaders");
        String[] fields = (nativeHeaders != null) ? nativeHeaders.toString().split("(?! )([, {}*]+)") : new String[] {};
        Map<String, String> rcvHeaders;
    }

    @MessageMapping("/send/chat")
    public void onReceivedChatMessage(SimpMessageHeaderAccessor accessor, String message) {
        System.out.println("Chat Message received!" + message);
        Object nativeHeaders = accessor.getMessageHeaders().get("nativeHeaders");
        String[] fields = (nativeHeaders != null) ? nativeHeaders.toString().split("(?! )([, {}*]+)") : new String[] {};
        Map<String, String> rcvHeaders;

        rcvHeaders = this.parseHeaders(fields);

        Map<String, Object> sndHeaders = new HashMap<>();
        sndHeaders.put("UserID", rcvHeaders.get("UserID"));
        sndHeaders.put("ideChat", rcvHeaders.get("ideChat"));
        this.template.convertAndSend(
                format("/chat/%s", rcvHeaders.get("room_id")), message, sndHeaders);
    }

    private Map<String, String> parseHeaders(String[] fields) {
        Map<String, String> headers = new HashMap<>();

        for (String field : fields) {
            if (!field.equals("")) {
                String[] headerComponents = field.split("=");
                System.out.println(field);
                headers.put(headerComponents[0], this.parseHeaderVal(headerComponents[1]));
            }
        }
        return headers;
    }

    private String parseHeaderVal(String value) {
        return String.join("", value.split("[\\[\\]]"));
    }
}
