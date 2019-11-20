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
    private String bus;
    private Double value;
    private Long timestamp;


    public static Message fromJson(String s) throws JsonProcessingException {
        return new ObjectMapper().readValue(s, Message.class);
    }
}