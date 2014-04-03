/**
 * ListResult.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 5:59:02 PM
 */
package com.xiaomi.mampa.redis.result;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;

/**
 * Value result from redis.
 * 
 * @author leo
 */
public class ValueResult<K, V> extends Result<K, V, V> {

    /**
     * @param codec
     */
    public ValueResult(IKeyValueCodec<K, V> codec) {
        super(codec, null);
    }

    @Override
    public ValueResult<K, V> set(byte[] value) {
        r = (value == null) ? null : codec.value().decode(value);
        return this;
    }
}
