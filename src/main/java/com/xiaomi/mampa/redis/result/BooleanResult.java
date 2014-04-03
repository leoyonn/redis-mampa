/**
 * BooleanResult.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 6:12:54 PM
 */
package com.xiaomi.mampa.redis.result;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;

/**
 * Boolean result from redis.
 * 
 * @author leo
 */
public class BooleanResult<K, V> extends Result<K, V, Boolean> {
    public BooleanResult(IKeyValueCodec<K, V> codec) {
        super(codec, null);
    }

    @Override
    public BooleanResult<K, V> set(long integer) {
        r = (integer == 1) ? Boolean.TRUE : Boolean.FALSE;
        return this;
    }

    @Override
    public BooleanResult<K, V> set(byte[] bytes) {
        r = (bytes != null) ? Boolean.TRUE : Boolean.FALSE;
        return this;
    }

}
