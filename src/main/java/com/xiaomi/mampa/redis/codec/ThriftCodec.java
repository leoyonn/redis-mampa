/**
 * ThriftCodec.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 3:05:32 PM
 */

package com.xiaomi.mampa.redis.codec;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import com.xiaomi.oms.common.Serializer;

/**
 * {@link ICodec} for Thrift.
 * 
 * @author leo
 */
public class ThriftCodec<T extends TBase<T, ?>> implements ICodec<T> {

    private Class<T> clazz;

    /**
     * Constructor.
     * 
     * @param clazz class of Thrift to be encoded and decoded.
     */
    public ThriftCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T decode(byte[] bytes) {
        try {
            return Serializer.decodeThrift(clazz, bytes, 0, bytes.length);
        } catch (TException ex) {
            return null;
        }
    }

    @Override
    public byte[] encode(T msg) {
        try {
            return Serializer.encodeThrift(msg);
        } catch (TException ex) {
            return null;
        }
    }

}
