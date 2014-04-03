/**

 * StringKeyValueCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 3:09:55 PM
 */
package com.xiaomi.mampa.redis.codec;

/**
 * {@link IKeyValueCodec} for String Key and String Value.
 * 
 * @author leo
 */
public class StringKeyValueCodec extends StringValueCodec<String> implements IKeyValueCodec<String, String> {
    public StringKeyValueCodec() {
        super(StringCodec.instance());
    }

    private static final StringKeyValueCodec instance = new StringKeyValueCodec();

    public static final StringKeyValueCodec instance() {
        return instance;
    }

    @Override
    public ICodec<String> key() {
        return StringCodec.instance();
    }

    @Override
    public ICodec<String> value() {
        return StringCodec.instance();
    }

}
