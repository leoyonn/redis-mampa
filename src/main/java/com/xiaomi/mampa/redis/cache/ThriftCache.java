/**
 * ThriftCache.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 2, 2014 7:27:07 PM
 */
package com.xiaomi.mampa.redis.cache;

import org.apache.thrift.TBase;

import com.xiaomi.mampa.redis.client.IClient;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringKeyThriftValueCodec;

/**
 * {@link ICache} with String Key and Thrift Value.
 * 
 * @author leo
 */
public class ThriftCache<T extends TBase<T, ?>> extends BaseCache<String, T> {
    /**
     * Constructor.
     * 
     * @param thriftClass Thrift-Class of value.
     * @param client
     */
    public ThriftCache(Class<T> thriftClass, IClient client) {
        super((IKeyValueCodec<String, T>) new StringKeyThriftValueCodec<T>(thriftClass), client);
    }

    /**
     * Constructor.
     * 
     * @param thriftClass Thrift-Class of value.
     * @param host
     * @param port
     */
    public ThriftCache(Class<T> thriftClass) {
        super((IKeyValueCodec<String, T>) new StringKeyThriftValueCodec<T>(thriftClass));
    }

}
