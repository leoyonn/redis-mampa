/**

 * StringKeyValueCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 3:09:55 PM
 */
package com.xiaomi.mampa.redis.codec;

import org.apache.thrift.TBase;

/**
 * {@link IKeyValueCodec} for String Key and Thrift Value.
 * 
 * @author leo
 */
public class StringKeyThriftValueCodec<T extends TBase<T, ?>> implements IKeyValueCodec<String, T> {
    private ThriftCodec<T> valueCodec;

    /**
     * Constructor. Value's type class is #thriftClass.
     * 
     * @param thriftClass
     */
    public StringKeyThriftValueCodec(Class<T> thriftClass) {
        this.valueCodec = new ThriftCodec<T>(thriftClass);
    }

    @Override
    public ICodec<String> key() {
        return StringCodec.instance();
    }

    @Override
    public ICodec<T> value() {
        return (ICodec<T>) valueCodec;
    }

}
