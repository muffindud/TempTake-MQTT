package com.github.muffindud.temptake.Models;

import java.nio.ByteBuffer;

public class MetaData {
    public int crc16;
    public byte[] manager_mac = new byte[6];
    public byte[] worker_mac = new byte[6];
    public long id;
    public int index_packet;
    public int total_packet_s;

    public static MetaData fromByteBuffer(ByteBuffer buffer) {
        MetaData metaData = new MetaData();
        metaData.crc16 = Short.toUnsignedInt(buffer.getShort());
        buffer.get(metaData.manager_mac);
        buffer.get(metaData.worker_mac);
        metaData.id = Integer.toUnsignedLong(buffer.getInt());
        metaData.index_packet = Byte.toUnsignedInt(buffer.get());
        metaData.total_packet_s = Byte.toUnsignedInt(buffer.get());
        return metaData;
    }
}
