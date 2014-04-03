/**
 * IClient.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 9:31:47 AM
 */
package com.xiaomi.mampa.redis.client;

import java.util.List;

import com.xiaomi.mampa.actor.Actor;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringValueCodec;
import com.xiaomi.mampa.redis.result.IResultReturner;

/**
 * Asynchronous redis-client using MAMPA({@link Actor}).
 * 
 * @author leo
 */
public interface IClient {

    /**
     * Ping the redis server, if server is OK, returner#success("PONG") will be called.
     * 
     * @see http://redis.io/commands/ping
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean ping(IResultReturner<String> returner);

    /**
     * Value's type of this key, can be "string" or "list"..
     * 
     * @see http://redis.io/commands/type
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K> boolean type(K key, StringValueCodec<K> codec, IResultReturner<String> returner);

    /**
     * If this key exists in redis.
     * 
     * @see http://redis.io/commands/exists
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean exists(K key, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * Set time-to-live of this key, after setting, this key will expire after #seconds.
     * 
     * @see http://redis.io/commands/expire
     * @param key
     * @param seconds
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean expire(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * @see http://redis.io/commands/get
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean get(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * CUSTOM DESIGNED redis-command: "get and expire". <BR>
     * It works as 'get key && expire key ttl' exept that the 'expire' command don't has any result reply;
     * <ul>
     * <li>if ttl > 0, set this key's time-to-live to ttl seconds;
     * <li>if ttl == 0, keep this key's time-to-live as it was;
     * <li>else (if ttl < 0), delete this key after get;
     * </ul>
     * In other words, Same semantic as:
     * 
     * <pre>
     * <code>
     *      v = get(key);
     *      if (v exists) {
     *          if (seconds > 0) {
     *              expire(key, seconds);
     *          } else if (seconds == 0) {
     *              // do nothing: keep ttl as it was
     *          } else if (seconds < 0) {
     *              delete(key);
     *          }
     *      }
     * </code>
     * </pre>
     * 
     * @param key
     * @param seconds
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean getex(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * @see http://redis.io/commands/set
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean set(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * Optimized upon standard redis-command:<BR>
     * The original version of this command means:<BR>
     * <ul>
     * <li>if ttl > 0, set this key's time-to-live to ttl seconds;
     * <li>else (if ttl <= 0) returns error message 'ERR invalid expire time in SETEX';
     * </ul>
     * <BR>
     * The modified version means:<BR>
     * <ul>
     * <li>if ttl > 0, set this key's time-to-live to ttl seconds (same as before);
     * <li>if ttl == 0, keep this key's time-to-live as it was;
     * <li>if ttl == -1, remove this key's ttl, mark key as has no expire;
     * <li>else returns error message '(error) ERR invalid expire time in SETEX, should be one of -1, 0, or
     * positive interger.'.
     * </ul>
     * 
     * @see http://redis.io/commands/setex
     * @param key
     * @param seconds
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later. The
     *         real processing result will return using the parameter #returner.
     */
    <K, V> boolean setex(K key, int seconds, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean setnx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * @see http://redis.io/commands/ttl
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean ttl(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lindex
     * @param key
     * @param index
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lindex(K key, int index, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * @see http://redis.io/commands/linsert
     * @param key
     * @param before
     * @param pivot
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean linsert(K key, boolean before, V pivot, V value, IKeyValueCodec<K, V> codec,
            IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/llen
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean llen(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lpop
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * @see http://redis.io/commands/lpush
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lpush
     * @param key
     * @param values
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lpushx
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lrange
     * @param key
     * @param start
     * @param stop
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lrange(K key, int start, int stop, IKeyValueCodec<K, V> codec, IResultReturner<List<V>> returner);

    /**
     * CUSTOM DESIGNED redis-command: "lrange and expire". <BR>
     * It works as 'lrange key && expire key ttl' exept that the 'expire' command don't has any result reply;
     * <ul>
     * <li>if ttl > 0, set this key's time-to-live to ttl seconds;
     * <li>if ttl == 0, keep this key's time-to-live as it was;
     * <li>else (if ttl < 0), delete this key after get;
     * </ul>
     * 
     * @see {@link #getex(K, int, IKeyValueCodec, IResultReturner)}
     * @param key
     * @param start
     * @param stop
     * @param seconds
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lrangeex(K key, int start, int stop, int seconds, IKeyValueCodec<K, V> codec,
            IResultReturner<List<V>> returner);

    /**
     * @see http://redis.io/commands/lrem
     * @param key
     * @param count
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lrem(K key, int count, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/lset
     * @param key
     * @param index
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean lset(K key, int index, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * @see http://redis.io/commands/ltrim
     * @param key
     * @param start
     * @param stop
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean ltrim(K key, int start, int stop, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner);

    /**
     * @see http://redis.io/commands/rpop
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean rpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * @see http://redis.io/commands/rpoplpush
     * @param src
     * @param dest
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean rpoplpush(K src, K dest, IKeyValueCodec<K, V> codec, IResultReturner<V> returner);

    /**
     * @see http://redis.io/commands/rpush
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean rpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/rpush
     * @param key
     * @param values
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean rpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/rpushx
     * @param key
     * @param value
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean rpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/del
     * @param key
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean del(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/del
     * @param keys
     * @param codec
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    <K, V> boolean del(List<K> keys, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner);

    /**
     * @see http://redis.io/commands/flushall
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean flushall(IResultReturner<Boolean> returner);

    /**
     * @see http://redis.io/commands/flushdb
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean flushdb(IResultReturner<Boolean> returner);

    /**
     * Shutdown this client.
     */
    void shutdown();
}
