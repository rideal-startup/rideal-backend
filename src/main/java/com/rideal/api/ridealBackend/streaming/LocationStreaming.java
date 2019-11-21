package com.rideal.api.ridealBackend.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rideal.api.ridealBackend.controllers.WebSocketController;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;

@Service
public class LocationStreaming {

    @Value("${rabbit-mq.host}")
    private String host;

    @Value("${rabbit-mq.port}")
    private Integer port;

    @Value("${rabbit-mq.user}")
    private String username;

    @Value("${rabbit-mq.password}")
    private String pass;


    private RMQSource<String> rmqSource(RMQConnectionConfig connectionConfig) {
        return new RMQSource<>(connectionConfig,
                "hello",
                false,
                new SimpleStringSchema());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        final var connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(host)
                .setUserName(username)
                .setPort(port)
                .setVirtualHost("/")
                .setPassword(pass)
                .build();

        final var stream = env
                .addSource(rmqSource(connectionConfig))
                .map(Message::fromJson)
                .assignTimestampsAndWatermarks(new TimestampAssigner())
                .keyBy(Message::getLineId)
                .timeWindow(Time.seconds(10), Time.seconds(1))
                .reduce(Message::mean)
                .addSink(new WebSocketSink());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

