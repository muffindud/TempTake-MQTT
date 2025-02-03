package com.github.muffindud.temptake.Models;

import java.nio.ByteBuffer;

public class MetaData {
    int crc16;
    byte[] manager_mac = new byte[6];
    byte[] worker_mac = new byte[6];
    int id;
    int index_packet;
    int total_packet_s;

    public static MetaData fromByteBuffer(ByteBuffer buffer) {
        MetaData metaData = new MetaData();
        metaData.crc16 = Short.toUnsignedInt(buffer.getShort());
        buffer.get(metaData.manager_mac);
        buffer.get(metaData.worker_mac);
        metaData.id = Short.toUnsignedInt(buffer.getShort());
        metaData.index_packet = Byte.toUnsignedInt(buffer.get());
        metaData.total_packet_s = Byte.toUnsignedInt(buffer.get());
        return metaData;
    }
}
