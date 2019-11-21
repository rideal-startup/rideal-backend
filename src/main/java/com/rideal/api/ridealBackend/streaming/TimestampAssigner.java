package com.rideal.api.ridealBackend.streaming;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

public class TimestampAssigner implements AssignerWithPeriodicWatermarks<Message> {

    private long currentMaxTimestamp;

    @Override
    public long extractTimestamp(Message element, long previousElementTimestamp) {
        long timestamp = element.getTimestamp();
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
        return timestamp;
    }

    @Override
    public Watermark getCurrentWatermark() {
        long maxOutOfOrderness = 3500;
        return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
    }
}
