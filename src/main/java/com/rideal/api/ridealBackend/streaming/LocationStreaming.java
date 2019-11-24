package com.rideal.api.ridealBackend.streaming;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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

    @Value("${rabbit-mq.vhost}")
    private String vhost;


    private RMQSource<String> rmqSource(RMQConnectionConfig connectionConfig) {
        return new RMQSource<>(connectionConfig,
                "hello",  false, new SimpleStringSchema());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        final var env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.enableCheckpointing(5000);

        final var connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(host)
                .setUserName(username)
                .setPort(port)
                .setVirtualHost(vhost)
                .setPassword(pass)
                .build();

        final var stream = env
                .addSource(rmqSource(connectionConfig))
                .map(Message::fromJson)
                .keyBy(Message::getLineId)
                .timeWindow(Time.seconds(5))
                .reduce(Message::mean)
                .addSink(new WebSocketSink())
                .setParallelism(1);

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

