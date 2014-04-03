/**
 * Utils.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 9:28:12 AM
 */
package com.xiaomi.mampa.redis.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

import com.xiaomi.oms.common.Serializer;

/**
 * Utils used for redis-mampa.
 * 
 * @author leo
 */
public class Utils extends Serializer {
    public static final byte[] CrLf = "\r\n".getBytes(ASCII);
    public static final ByteBufAllocator bufPool = new PooledByteBufAllocator();

    public static ByteBuf allocByteBuf(int initialCapacity) {
        return bufPool.buffer(initialCapacity);
    }

    /**
     * Write the textual value of a positive integer to the supplied buffer.
     * 
     * @param buf Buffer to write to.
     * @param value Value to write.
     */
    public static void writeLongAsStringV2(ByteBuf buf, long value) {
        if (value < 10) {
            buf.writeByte('0' + (int) value);
            return;
        }
        buf.writeBytes(String.valueOf(value).getBytes(ASCII));
    }

    /**
     * Write the textual value of a positive integer to the supplied buffer.
     * 
     * @param buf Buffer to write to.
     * @param value Value to write.
     */
    public static void writeLongAsString(ByteBuf buf, long value) {
        if (value < 10) {
            buf.writeByte('0' + (int) value);
            return;
        }

        StringBuilder sb = new StringBuilder(8);
        while (value > 0) {
            int digit = (int) (value % 10);
            sb.append((char) ('0' + digit));
            value /= 10;
        }

        for (int i = sb.length() - 1; i >= 0; i--) {
            buf.writeByte(sb.charAt(i));
        }
    }
}
