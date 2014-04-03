/**
 * BooleanResult.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 6:12:54 PM
 */
package com.xiaomi.mampa.redis.result;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;

/**
 * Integer result from redis.
 * 
 * @author leo
 */
public class IntegerResult<K, V> extends Result<K, V, Integer> {

    public IntegerResult(IKeyValueCodec<K, V> codec) {
        super(codec, null);
    }

    @Override
    public IntegerResult<K, V> set(long integer) {
        r = (int) integer;
        return this;
    }

    @Override
    public IntegerResult<K, V> set(byte[] bytes) {
        r = null;
        return this;
    }
}
