/**
 * ICache.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 2, 2014 11:48:55 AM
 */
package com.xiaomi.mampa.redis.cache;

import java.util.List;

import com.xiaomi.mampa.redis.client.IClient;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.result.IResultReturner;

/**
 * Interface for cache, mainly supporting <K, V> and <K, List<V>> types.
 * 
 * @author leo
 */
public interface ICache<K, V> {
    /**
     * Value type of a key in cache.
     * 
     * @author leo
     */
    public static enum ValueType {
        None, Simple, List, Invalid;

        /**
         * Parse type from string into this enum, only partial supported.
         * 
         * @param typeString
         * @return
         */
        public static ValueType parse(String typeString) {
            if ("none".equals(typeString)) {
                return None;
            } else if ("string".equals(typeString)) {
                return Simple;
            } else if ("list".equals(typeString)) {
                return List;
            }
            return Invalid;
        }
    }

    /**
     * {@link IKeyValueCodec} for key/value's encoding and decoding.
     * 
     * @return
     */
    IKeyValueCodec<K, V> codec();

    /**
     * Client connecting to redis server.
     * 
     * @return
     */
    IClient client();

    /**
     * Ping the redis server, if server is OK, returner#success(true) will be called.
     * 
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean ping(IResultReturner<Boolean> returner);

    /**
     * Value's type of this key, can be "string" or "list"..
     * 
     * @see {@link ValueType}
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
	 *		   The real processing result will return using the parameter #returner.
     */
    boolean valueType(K key, IResultReturner<ValueType> returner);

    /**
     * If this key exists in cache.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
	 *		   The real processing result will return using the parameter #returner.
     */
    boolean exists(K key, IResultReturner<Boolean> returner);

    /**
     * Delete this key from cache.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
	 *		   The real processing result will return using the parameter #returner.
     */
    boolean del(K key, IResultReturner<Integer> returner);

    /**
     * Delete these keys from cache.
     * @param keys
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
	 *		   The real processing result will return using the parameter #returner.
     */
    boolean del(List<K> keys, IResultReturner<Integer> returner);

    /**
     * Set time-to-live of this key, after setting, this key will expire after #seconds.
     * 
     * @param key
     * @param seconds
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
	 *		   The real processing result will return using the parameter #returner.
     */
    boolean setTtl(K key, int seconds, IResultReturner<Boolean> returner);

    /**
     * Get value of key holding {@link ValueType#Simple}.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean get(K key, IResultReturner<V> returner);

    /**
     * Same semantic as:
     * 
     * <pre>
     * <code>
     *      v = get(key);
     *      if (v exists) {
     *          if (seconds > 0) {
     *              setTtl(key, seconds);
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
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean getAndUpdateTtl(K key, int seconds, IResultReturner<V> returner);

    /**
     * Set value to a key holding nothing or {@link ValueType#Simple}.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean set(K key, V value, IResultReturner<Boolean> returner);

    /**
     * Set value to a key holding nothing or {@link ValueType#Simple}, and set it's time-to-live to #seconds.
     * 
     * @param key
     * @param seconds
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean setWithTtl(K key, int seconds, V value, IResultReturner<Boolean> returner);

    /**
     * Set value to this key only if the key already exists in cache.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean setIfNotExists(K key, V value, IResultReturner<Boolean> returner);

    /**
     * Get time-to-live of this key.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean ttl(K key, IResultReturner<Integer> returner);

    /**
     * Get the #index'th value of this key holding {@link ValueType#List}. <BR>
     * If #index is out-of-bound, just returns null (no exception).
     * 
     * @param key
     * @param index
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean elementAt(K key, int index, IResultReturner<V> returner);

    /**
     * Set the #index'th value of this key holding {@link ValueType#List}. <BR>
     * If #index is out-of-bound, #returner.fail("ERR index out of range") will be called.
     * 
     * @param key
     * @param index
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean setAt(K key, int index, V value, IResultReturner<Boolean> returner);

    /**
     * Insert a value before/after the <strong>FIRST<strong> pivot into a key holding {@link ValueType#List}. <BR>
     * If #pivot doesn't exists in this key, returns -1 meaning insert not succeeded.
     * 
     * @param key
     * @param before
     * @param pivot
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean insert(K key, boolean before, V pivot, V value, IResultReturner<Integer> returner);

    /**
     * List size of this key holding {@link ValueType#List}. <BR>
     * Same as {@link #length(K, IResultReturner)}
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean size(K key, IResultReturner<Integer> returner);

    /**
     * List size of this key holding {@link ValueType#List}. <BR>
     * Same as {@link #size(K, IResultReturner)}
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean length(K key, IResultReturner<Integer> returner);

    /**
     * Put a value at the head of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offerHead(K key, V value, IResultReturner<Integer> returner);

    /**
     * Put values at the head of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param values
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offerHead(K key, List<V> values, IResultReturner<Integer> returner);

    /**
     * Put a value at the head of list({@link ValueType#List}) holding by this key, if this key exists.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offerHeadIfExists(K key, V value, IResultReturner<Integer> returner);

    /**
     * Put a value at the tail of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offer(K key, V value, IResultReturner<Integer> returner);

    /**
     * Put values at the tail of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param values
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offer(K key, List<V> values, IResultReturner<Integer> returner);

    /**
     * Put a value at the tail of list({@link ValueType#List}) holding by this key if key exists in cache.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean offerIfExists(K key, V value, IResultReturner<Integer> returner);

    /**
     * Get and delete first value at the head of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean poll(K key, IResultReturner<V> returner);

    /**
     * Get and delete first value at the tail of list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean pollTail(K key, IResultReturner<V> returner);

    /**
     * Remove all values equals to #value in the list({@link ValueType#List}) holding by this key.
     * #returner will returns number of removed values.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean remove(K key, V value, IResultReturner<Integer> returner);

    /**
     * Remove the first value(nearest head) equals to #value in the list({@link ValueType#List}) holding by this key.<BR>
     * #returner will returns number of removed values.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean removeFirst(K key, V value, IResultReturner<Integer> returner);

    /**
     * Remove the last value(nearest tail) equals to #value in the list({@link ValueType#List}) holding by this key.
     * #returner will returns number of removed values.
     * 
     * @param key
     * @param value
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean removeLast(K key, V value, IResultReturner<Integer> returner);

    /**
     * Get all elements in the list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean elements(K key, IResultReturner<List<V>> returner);

    /**
     * Retains #length elements from start and delete all others in the list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param start
     * @param length
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean retains(K key, int start, int length, IResultReturner<Boolean> returner);

    /**
     * Get sub-list elements in the list({@link ValueType#List}) holding by this key.
     * 
     * @param key
     * @param start
     * @param length
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean sublist(K key, int start, int length, IResultReturner<List<V>> returner);

    /**
     * Same semantic as sublist(...) and setTtl(...).
     * 
     * @see #getAndUpdateTtl(Object, int, IResultReturner)
     * @param key
     * @param start
     * @param length
     * @param seconds
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean sublistAndUpdateTtl(K key, int start, int length, int seconds, IResultReturner<List<V>> returner);

    /**
     * <strong>BE CAREFUL! MAKE SURE YOU KNOW WHAT YOU ARE DOING:</strong><br>
     * Delete <strong>ALL<strong> data from cache!
     * 
     * @param returner
     * @return True if command will be processed, false means client too busy, you may try again later.
     *         The real processing result will return using the parameter #returner.
     */
    boolean deleteAllData(IResultReturner<Boolean> returner);

    /**
     * Shutdown this client to cache-server, not server.
     */
    void shutdown();

}
