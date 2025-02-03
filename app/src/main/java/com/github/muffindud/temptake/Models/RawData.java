package com.github.muffindud.temptake.Models;

import java.nio.ByteBuffer;

public class RawData {
    public float temperature;
    public float humidity;
    public float pressure;
    public float ppm;

    public static RawData fromByteBuffer(ByteBuffer buffer) {
        RawData rawData = new RawData();
        rawData.temperature = (float) buffer.getLong() / 100.0f - 40.0f;
        rawData.humidity = (float) buffer.getLong() / 100.0f;
        rawData.pressure = (float) buffer.getLong() / 100.0f;
        rawData.ppm = (float) buffer.getLong() / 100.0f;
        return rawData;
    }
}
