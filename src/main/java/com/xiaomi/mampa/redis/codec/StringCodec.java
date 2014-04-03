/**
 * StringCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 3:05:32 PM
 */
package com.xiaomi.mampa.redis.codec;

import com.xiaomi.mampa.redis.utils.Utils;

/**
 * Codec for String.
 * 
 * @author leo
 */
public class StringCodec implements ICodec<String> {
    private static final StringCodec instance = new StringCodec();

    public static final StringCodec instance() {
        return instance;
    }

    @Override
    public String decode(byte[] bytes) {
        return Utils.decodeString(bytes);
    }

    @Override
    public byte[] encode(String msg) {
        return Utils.encodeString(msg);
    }

}
