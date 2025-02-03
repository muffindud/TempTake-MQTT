package com.github.muffindud.temptake.Models;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataPacket {
    int type;
    MetaData metaData;
    int length;
    RawData rawData;

    public static DataPacket fromBinary(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        DataPacket dataPacket = new DataPacket();
        dataPacket.type = Byte.toUnsignedInt(buffer.get());
        dataPacket.metaData = MetaData.fromByteBuffer(buffer);
        dataPacket.length = Byte.toUnsignedInt(buffer.get());

        byte[] rawDataBytes = new byte[dataPacket.length];
        buffer.get(rawDataBytes);

        ByteBuffer rawDataBuffer = ByteBuffer.wrap(rawDataBytes).order(ByteOrder.LITTLE_ENDIAN);
        dataPacket.rawData = RawData.fromByteBuffer(rawDataBuffer);

        return dataPacket;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "type=" + type +
                ", metaData=" + metaData +
                ", length=" + length +
                ", rawData=" + rawData +
                '}';
    }
}
