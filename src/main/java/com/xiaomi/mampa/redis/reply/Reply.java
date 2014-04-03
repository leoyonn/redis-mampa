/**
 * Reply.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 3:13:21 PM
 */
package com.xiaomi.mampa.redis.reply;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.xiaomi.mampa.redis.utils.Utils;

/**
 * Reply protocol and Raw define from redis.
 * 
 * @author leo
 */
public class Reply {
    /**
     * Reply.Raw contains bytes from redis-server.
     * 
     * @author leo
     */
    public static class Raw {
        private static final int InvalidInt = Integer.MIN_VALUE;
        private byte[] result;
        private List<byte[]> results;
        private int intResult = InvalidInt;
        private byte[] error;
        private Type type = Type.Invalid;

        public Raw clear() {
            this.type = Type.Invalid;
            this.result = null;
            this.error = null;
            this.intResult = InvalidInt;
            return this;
        }

        /**
         * Set result int raw for further operations such as decoding.
         * 
         * @param result
         * @return
         */
        public Raw result(byte[] result) {
            switch (type) {
                case Array: {
                    this.results.add(result);
                    break;
                }
                case Simple: {
                    this.result = result;
                    break;
                }
                default: {
                    this.result = result;
                    this.type = Type.Simple;
                    break;
                }
            }
            return this;
        }

        /**
         * Set integer result into raw.
         * 
         * @param result
         * @return
         */
        public Raw result(int result) {
            this.intResult = result;
            this.type = Type.Int;
            return this;
        }

        public byte[] result() {
            return result;
        }
        
        public List<byte[]> results() {
            return results;
        }

        public int intResult() {
            return intResult;
        }

        public byte[] error() {
            return error;
        }

        /**
         * Set error into raw.
         * 
         * @param error
         * @return
         */
        public Raw error(byte[] error) {
            this.error = error;
            this.type = Type.Error;
            return this;
        }

        public boolean hasError() {
            return error != null;
        }

        public boolean valid() {
            return type != Type.Invalid;
        }

        public Type type() {
            return type;
        }

        /**
         * Mark this reply is an array.
         * 
         * @return
         */
        public Raw arrayBegin() {
            this.results = new ArrayList<byte[]>();
            this.type = Type.Array;
            return this;
        }

        /**
         * Mark this array-reply is finished.
         * 
         * @return
         */
        public Raw arrayDone() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<Valid:").append(valid());
            if (error != null) {
                sb.append(",Error:").append(Utils.decodeAscii(error));
            }
            if (result != null) {
                sb.append(",Result:").append(Utils.decodeString(result));
            }
            if (results != null) {
                sb.append(",Results:[");
                for (byte[] result : results) {
                    sb.append(Utils.decodeString(result)).append(',');
                }
                sb.append("]");
            }
            if (intResult != InvalidInt) {
                sb.append(",IntResult:").append(intResult);
            }
            return sb.append(">").toString();
        }
    }

    /**
     * Redis reply type.
     * <pre>
     *  For Simple Strings the first byte of the reply is "+"
     *  For Errors the first byte of the reply is "-"
     *  For Integers the first byte of the reply is ":"
     *  For Bulk Strings the first byte of the reply is "$"
     *  For Arrays the first byte of the reply is "*"
     * </pre>
     * 
     * @author leo
     */
    public static enum Type {
        Simple  ((byte)'+'),
        Error   ((byte)'-'),
        Int     ((byte)':'),
        Bulk    ((byte)'$'),
        Array   ((byte)'*'),
        Bytes   ((byte)'"'), // expended for ReplyFsm
        Invalid ((byte)-1);
    
        private final byte b;
        private Type(byte b) {
            this.b = b;
        }
        
        public byte b() {
            return b;
        }
    }

    /**
     * Parse Type from byte.
     *  
     * @param b
     * @return
     */
    public static Type type(byte b) {
        switch(b) {
        case '+': return Type.Simple;
        case '-': return Type.Error;
        case ':': return Type.Int;
        case '$': return Type.Bulk;
        case '*': return Type.Array;
        case '"': return Type.Bytes;
        default:  throw new IllegalStateException("Invalid redis reply type: " + (char)b);
        }
    }

    /**
     * Find {@link Utils#CrLf} from redis-reply-ByteBuf.
     * 
     * @param buf
     * @return
     */
    public static int findLineEnd(ByteBuf buf) {
        int start = buf.readerIndex();
        int index = buf.indexOf(start, buf.writerIndex(), (byte) '\n');
        return (index > 0 && buf.getByte(index - 1) == '\r') ? index : -1;
    }

    /**
     * Read a long value from redis-reply-ByteBuf.
     * 
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public static long readLong(ByteBuf buf, int start, int end) {
        long value = 0;
        boolean negative = buf.getByte(start) == '-';
        int offset = negative ? start + 1 : start;
        while (offset < end - 1) {
            int digit = buf.getByte(offset++) - '0';
            value = value * 10 - digit;
        }
        if (!negative) {
            value = -value;
        }
        buf.readerIndex(end + 1);
        return value;
    }

    /**
     * Read a whole line from redis-reply-ByteBuf.
     * 
     * @param buf
     * @return
     */
    public static byte[] readLine(ByteBuf buf) {
        int end = findLineEnd(buf);
        if (end < -1) {
            return null;
        }
        byte[] bytes = null;
        int start = buf.readerIndex(), len = end - start - 1;
        bytes = new byte[len];
        buf.getBytes(start, bytes, 0, len);
        buf.readerIndex(end + 1);
        return bytes;
    }

    /**
     * Read a bunch of #count bytes from redis-reply-ByteBuf.
     * 
     * @param buf
     * @param count
     * @return
     */
    public static byte[] readBytes(ByteBuf buf, int count) {
        byte[] bytes = null;
        if (buf.readableBytes() < count) {
            return null;
        }
        bytes = new byte[count - 2];
        buf.getBytes(buf.readerIndex(), bytes, 0, count - 2);
        buf.readerIndex(buf.readerIndex() + count);
        return bytes;
    }
}
