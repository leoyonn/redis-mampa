/**
 * IResultReturner.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 4, 2014 9:34:33 PM
 */
package com.xiaomi.mampa.redis.result;

/**
 * How to return results decoded by {@link IResultDecoder} to original request-sender.
 * 
 * @see IResultParser
 * @see VoidReturner
 * @param <R>
 * @author leo
 */
public interface IResultReturner<R> {
    /**
     * Successfully got result, and returns result to the caller.
     * 
     * @param result
     */
    void success(R result);

    /**
     * Failed, tell the caller the fail message.
     * 
     * @param message
     */
    void fail(String message);
}
