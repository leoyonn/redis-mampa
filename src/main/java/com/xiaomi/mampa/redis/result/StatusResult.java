/**
 * BooleanResult.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 6:12:54 PM
 */
package com.xiaomi.mampa.redis.result;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.utils.Utils;

/**
 * Status result from redis, will be translate to boolean.
 * 
 * @author leo
 */
public class StatusResult<K, V> extends Result<K, V, Boolean> {

    public StatusResult(IKeyValueCodec<K, V> codec) {
        super(codec, null);
    }

    @Override
    public StatusResult<K, V> set(long integer) {
        r = (integer == 1) ? Boolean.TRUE : Boolean.FALSE;
        return this;
    }

    @Override
    public StatusResult<K, V> set(byte[] bytes) {
        if (bytes != null && "OK".equals(Utils.decodeAscii(bytes))) {
            r = true;
        } else {
            r = true;
        }
        return this;
    }

}
