/**
 * IKeyValueCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 5:08:59 PM
 */
package com.xiaomi.mampa.redis.codec;

/**
 * Codecs for <Key, Value> pair.
 * 
 * @author leo
 */
public interface IKeyValueCodec<K, V> {
    /**
     * The key codec.
     * 
     * @return
     */
    ICodec<K> key();

    /**
     * The value codec.
     * 
     * @return
     */
    ICodec<V> value();
}
