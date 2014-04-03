/**
 * DummyRedis.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 4:38:50 PM
 */
package com.xiaomi.mampa.redis.mock;

import java.util.List;

import com.xiaomi.mampa.redis.client.IClient;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringValueCodec;
import com.xiaomi.mampa.redis.result.IResultReturner;

/**
 * Dummy redis just returns nothing.
 * 
 * @author leo
 */
public class DummyRedis implements IClient {
    public static DummyRedis get() {
        return new DummyRedis();
    }

    @Override
    public <K, V> boolean exists(K key, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean expire(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean get(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean getex(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean set(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean setex(K key, int seconds, V value, IKeyValueCodec<K, V> codec,
            IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean setnx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public boolean ping(IResultReturner<String> returner) {
        returner.success("PONG");
        return true;
    }

    @Override
    public <K> boolean type(K key, StringValueCodec<K> codec, IResultReturner<String> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean ttl(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(-2);
        return true;
    }

    @Override
    public <K, V> boolean lindex(K key, int index, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean linsert(K key, boolean before, V pivot, V value, IKeyValueCodec<K, V> codec,
            IResultReturner<Integer> returner) {
        returner.success(-1);
        return true;
    }

    @Override
    public <K, V> boolean llen(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(0);
        return true;
    }

    @Override
    public <K, V> boolean lpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean lpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(-1);
        return true;
    }

    @Override
    public <K, V> boolean lpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(-1);
        return true;
    }

    @Override
    public <K, V> boolean lrange(K key, int start, int stop, IKeyValueCodec<K, V> codec,
            IResultReturner<List<V>> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean lrangeex(K key, int start, int stop, int seconds, IKeyValueCodec<K, V> codec,
            IResultReturner<List<V>> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean lrem(K key, int count, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean lset(K key, int index, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean ltrim(K key, int start, int stop, IKeyValueCodec<K, V> codec,
            IResultReturner<Boolean> returner) {
        returner.success(false);
        return true;
    }

    @Override
    public <K, V> boolean rpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean rpoplpush(K src, K dest, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        returner.success(null);
        return true;
    }

    @Override
    public <K, V> boolean rpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(-1);
        return true;
    }

    @Override
    public <K, V> boolean rpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(-1);
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean flushall(IResultReturner<Boolean> returner) {
        returner.success(true);
        return true;
    }

    @Override
    public boolean flushdb(IResultReturner<Boolean> returner) {
        returner.success(true);
        return true;
    }

    @Override
    public <K, V> boolean del(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(0);
        return true;
    }

    @Override
    public <K, V> boolean del(List<K> keys, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(0);
        return true;
    }

    @Override
    public <K, V> boolean lpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(0);
        return true;
    }

    @Override
    public <K, V> boolean rpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        returner.success(0);
        return true;
    }
}
