package com.rideal.api.ridealBackend.streaming;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

public class TimestampAssigner implements AssignerWithPeriodicWatermarks<Message> {

    @Override
    public long extractTimestamp(Message element, long previousElementTimestamp) {
        return element.getTimestamp();
    }

    @Override
    public Watermark getCurrentWatermark() {
        return null;
    }
}
