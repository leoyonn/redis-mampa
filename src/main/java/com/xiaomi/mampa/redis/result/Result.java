/**
 * Result.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 5:12:10 PM
 */
package com.xiaomi.mampa.redis.result;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.utils.Utils;

/**
 * Base defination of result from redis.
 * 
 * @author leo
 */
public abstract class Result<K, V, R> {
    protected IKeyValueCodec<K, V> codec;
    protected R r;
    protected String error;

    /**
     * Initialize a new instance that encodes and decodes keys and values using the supplied codec.
     * 
     * @param codec Codec used to encode/decode keys and values.
     * @param r Initial value of output.
     */
    public Result(IKeyValueCodec<K, V> codec, R r) {
        this.codec = codec;
        this.r = r;
    }

    /**
     * Get the command output.
     * 
     * @return The command output.
     */
    public R get() {
        return r;
    }

    /**
     * Set the command output to a sequence of bytes, or null. Concrete {@link Result} implementations must
     * override this method unless they only receive an integer value which cannot be null.
     * 
     * @param bytes The command output, or null.
     */
    public Result<K, V, R> set(byte[] bytes) {
        throw new IllegalStateException();
    }

    /**
     * Set the command output to a 64-bit signed integer. Concrete {@link Result} implementations must
     * override this method unless they only receive a byte array value.
     * 
     * @param integer The command output.
     */
    public Result<K, V, R> set(long integer) {
        throw new IllegalStateException();
    }

    /**
     * Set command output to an error message from the server.
     * 
     * @param error Error message.
     */
    public Result<K, V, R> setError(byte[] bytes) {
        this.error = Utils.decodeAscii(bytes);
        return this;
    }

    /**
     * Set command output to an error message from the client.
     * 
     * @param error Error message.
     */
    public Result<K, V, R> setError(String error) {
        this.error = error;
        return this;
    }

    /**
     * Check if the command resulted in an error.
     * 
     * @return true if command resulted in an error.
     */
    public boolean hasError() {
        return this.error != null;
    }

    /**
     * Get the error that occurred.
     * 
     * @return The error.
     */
    public String error() {
        return error;
    }
}
