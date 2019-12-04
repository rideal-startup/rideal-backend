package com.rideal.api.ridealBackend.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String lineId;
    private Double lat;
    private Double lng;
    private Long timestamp;

    public Message mean(Message other) {
        return new Message(other.lineId,
                (other.lat + this.lat) / 2,
                (other.lng + this.lng) / 2,
                other.timestamp);
    }

    public static Message fromJson(String s) throws JsonProcessingException {
        return new ObjectMapper().readValue(s, Message.class);
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}