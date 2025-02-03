package com.github.muffindud.temptake.Models;

import java.nio.ByteBuffer;

public class RawData {
    long temperature;
    long humidity;
    long pressure;
    long ppm;

    public static RawData fromByteBuffer(ByteBuffer buffer) {
        RawData rawData = new RawData();
        rawData.temperature = buffer.getLong();
        rawData.humidity = buffer.getLong();
        rawData.pressure = buffer.getLong();
        rawData.ppm = buffer.getLong();
        return rawData;
    }
}
