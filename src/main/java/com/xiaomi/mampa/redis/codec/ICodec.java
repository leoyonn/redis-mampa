/**
 * ICodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 9:41:08 AM
 */
package com.xiaomi.mampa.redis.codec;

/**
 * Codec interface for decoding and encoding.
 * 
 * @author leo
 */
public interface ICodec<T> {
    /**
     * Decode from bytes to T.
     * 
     * @param bytes
     * @return
     */
    T decode(byte[] bytes);

    /**
     * Encoding from T to bytes.
     * 
     * @param msg
     * @return
     */
    byte[] encode(T msg);
}
