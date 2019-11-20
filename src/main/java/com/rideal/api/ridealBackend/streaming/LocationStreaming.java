package com.rideal.api.ridealBackend.streaming;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    private Message average(Message m1, Message m2) {
        return new Message(m1.getBus(),
                (m1.getValue() + m2.getValue()) / 2,
                0L);
    }

    @PostConstruct
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
                .keyBy(Message::getBus)
                .timeWindow(Time.seconds(10), Time.seconds(5))
                .reduce(this::average);

        stream.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

