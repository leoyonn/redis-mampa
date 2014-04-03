/**

 * StringKeyValueCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 3:09:55 PM
 */
package com.xiaomi.mampa.redis.codec;

/**
 * {@link IKeyValueCodec} for arbitrary Key and String Value (For excample, can be used for status-reply from redis).
 * 
 * @author leo
 */
public class StringValueCodec<K> implements IKeyValueCodec<K, String> {
    private ICodec<K> keyCodec;

    public StringValueCodec(ICodec<K> keyCodec) {
        this.keyCodec = keyCodec;
    }

    @Override
    public ICodec<K> key() {
        return keyCodec;
    }

    @Override
    public ICodec<String> value() {
        return StringCodec.instance();
    }

}
