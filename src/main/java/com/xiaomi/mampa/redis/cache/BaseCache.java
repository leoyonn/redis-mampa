/**
 * BaseCache.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 2, 2014 2:39:54 PM
 */
package com.xiaomi.mampa.redis.cache;

import java.util.List;

import com.xiaomi.mampa.redis.client.ClientFactory;
import com.xiaomi.mampa.redis.client.IClient;
import com.xiaomi.mampa.redis.client.RedisMampaClient;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringValueCodec;
import com.xiaomi.mampa.redis.result.IResultReturner;

/**
 * Basic cache implementation using redis client {@link RedisMampaClient}.
 * 
 * @author leo
 */
public abstract class BaseCache<K, V> implements ICache<K, V> {
    private IKeyValueCodec<K, V> codec;
    private StringValueCodec<K> stringValueCodec;
    private IClient client;

    /**
     * Constructor.
     * 
     * @param codec
     * @param client
     */
    @SuppressWarnings("unchecked")
    public BaseCache(IKeyValueCodec<K, V> codec, IClient client) {
        this.codec = codec;
        this.client = client;
        if (codec instanceof StringValueCodec<?>) {
            this.stringValueCodec = (StringValueCodec<K>) codec;
        } else {
            this.stringValueCodec = new StringValueCodec<K>(codec.key());
        }
    }

    /**
     * Constructor.
     * 
     * @param codec
     * @param host
     * @param port
     */
    public BaseCache(IKeyValueCodec<K, V> codec) {
        this(codec, ClientFactory.getLocalClient());
    }

    @Override
    public IKeyValueCodec<K, V> codec() {
        return codec;
    }

    @Override
    public IClient client() {
        return client;
    }

    @Override
    public boolean ping(final IResultReturner<Boolean> returner) {
        return client.ping(new IResultReturner<String>() {
            @Override
            public void success(String result) {
                returner.success("PONG".equals(result));
            }

            @Override
            public void fail(String message) {
                returner.fail(message);
            }

        });
    }

    @Override
    public boolean valueType(K key, final IResultReturner<ValueType> returner) {
        return client.type(key, stringValueCodec, new IResultReturner<String>() {
            @Override
            public void success(String result) {
                returner.success(ValueType.parse(result));
            }

            @Override
            public void fail(String message) {
                returner.fail(message);
            }
        });
    }

    @Override
    public boolean exists(K key, IResultReturner<Boolean> returner) {
        return client.exists(key, codec, returner);
    }

    @Override
    public boolean del(K key, IResultReturner<Integer> returner) {
        return client.del(key, codec, returner);
    }

    @Override
    public boolean del(List<K> keys, IResultReturner<Integer> returner) {
        return client.del(keys, codec, returner);
    }

    @Override
    public boolean setTtl(K key, int seconds, IResultReturner<Boolean> returner) {
        return client.expire(key, seconds, codec, returner);
    }

    @Override
    public boolean get(K key, IResultReturner<V> returner) {
        return client.get(key, codec, returner);
    }

    @Override
    public boolean getAndUpdateTtl(K key, int seconds, IResultReturner<V> returner) {
        return client.getex(key, seconds, codec, returner);
    }

    @Override
    public boolean set(K key, V value, IResultReturner<Boolean> returner) {
        return client.set(key, value, codec, returner);
    }

    @Override
    public boolean setWithTtl(K key, int seconds, V value, IResultReturner<Boolean> returner) {
        return client.setex(key, seconds, value, codec, returner);
    }

    @Override
    public boolean setIfNotExists(K key, V value, IResultReturner<Boolean> returner) {
        return client.setnx(key, value, codec, returner);
    }

    @Override
    public boolean ttl(K key, IResultReturner<Integer> returner) {
        return client.ttl(key, codec, returner);
    }

    @Override
    public boolean elementAt(K key, int index, IResultReturner<V> returner) {
        return client.lindex(key, index, codec, returner);
    }

    @Override
    public boolean setAt(K key, int index, V value, IResultReturner<Boolean> returner) {
        return client.lset(key, index, value, codec, returner);
    }

    @Override
    public boolean insert(K key, boolean before, V pivot, V value, IResultReturner<Integer> returner) {
        return client.linsert(key, before, pivot, value, codec, returner);
    }

    @Override
    public boolean size(K key, IResultReturner<Integer> returner) {
        return client.llen(key, codec, returner);
    }

    @Override
    public boolean length(K key, IResultReturner<Integer> returner) {
        return size(key, returner);
    }

    @Override
    public boolean offerHead(K key, V value, IResultReturner<Integer> returner) {
        return client.lpush(key, value, codec, returner);
    }

    @Override
    public boolean offerHead(K key, List<V> values, IResultReturner<Integer> returner) {
        return client.lpush(key, values, codec, returner);
    }

    @Override
    public boolean offerHeadIfExists(K key, V value, IResultReturner<Integer> returner) {
        return client.lpushx(key, value, codec, returner);
    }

    @Override
    public boolean offer(K key, V value, IResultReturner<Integer> returner) {
        return client.rpush(key, value, codec, returner);
    }

    @Override
    public boolean offer(K key, List<V> values, IResultReturner<Integer> returner) {
        return client.rpush(key, values, codec, returner);
    }

    @Override
    public boolean offerIfExists(K key, V value, IResultReturner<Integer> returner) {
        return client.rpushx(key, value, codec, returner);
    }

    @Override
    public boolean poll(K key, IResultReturner<V> returner) {
        return client.lpop(key, codec, returner);
    }

    @Override
    public boolean pollTail(K key, IResultReturner<V> returner) {
        return client.rpop(key, codec, returner);
    }

    @Override
    public boolean remove(K key, V value, IResultReturner<Integer> returner) {
        return client.lrem(key, 0, value, codec, returner);
    }

    @Override
    public boolean removeFirst(K key, V value, IResultReturner<Integer> returner) {
        return client.lrem(key, 1, value, codec, returner);
    }

    @Override
    public boolean removeLast(K key, V value, IResultReturner<Integer> returner) {
        return client.lrem(key, -1, value, codec, returner);
    }

    @Override
    public boolean elements(K key, IResultReturner<List<V>> returner) {
        return client.lrange(key, 0, -1, codec, returner);
    }

    @Override
    public boolean retains(K key, int start, int length, IResultReturner<Boolean> returner) {
        return client.ltrim(key, start, start + length - 1, codec, returner);
    }

    @Override
    public boolean sublist(K key, int start, int length, IResultReturner<List<V>> returner) {
        return client.lrange(key, start, start + length - 1, codec, returner);
    }

    @Override
    public boolean sublistAndUpdateTtl(K key, int start, int length, int seconds, IResultReturner<List<V>> returner) {
        return client.lrangeex(key, start, start + length - 1, seconds, codec, returner);
    }

    @Override
    public boolean deleteAllData(IResultReturner<Boolean> returner) {
        return client.flushdb(returner);
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
        codec = null;
        stringValueCodec = null;
    }
}
