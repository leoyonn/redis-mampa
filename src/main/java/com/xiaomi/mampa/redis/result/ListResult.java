/**
 * ListResult.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 5:59:02 PM
 */
package com.xiaomi.mampa.redis.result;

import java.util.ArrayList;
import java.util.List;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;

/**
 * List result from redis.
 * 
 * @author leo
 */
public class ListResult<K, V> extends Result<K, V, List<V>> {

    /**
     * @param codec
     */
    public ListResult(IKeyValueCodec<K, V> codec) {
        super(codec, new ArrayList<V>());
    }

    @Override
    public ListResult<K, V> set(byte[] value) {
        r.add(codec.value().decode(value));
        return this;
    }
}
