package com.rideal.api.ridealBackend.controllers;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate template;

    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("New user connected");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        System.out.println("User disconnected");
    }

    @MessageMapping("/send/chat")
    public void onReceivedChatMessage(SimpMessageHeaderAccessor accessor, String message) {
        System.out.println("Chat Message received!" + message);
        Object nativeHeaders = accessor.getMessageHeaders().get("nativeHeaders");
        String[] fields = (nativeHeaders != null) ? nativeHeaders.toString().split("(?! )([, {}*]+)") : new String[]{};
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
