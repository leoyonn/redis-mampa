/**
 * PerfConstants.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 18, 2014 3:14:16 PM
 */
package com.xiaomi.mampa.redis.utils;



/**
 * Constants for perf-counter
 * 
 * @author leo
 */
public interface PerfConstants {
    String Prefix = "redis~mampa~";

    String Reply = Prefix + "reply~";
    String ReplyBegin = Reply + "begin";
    String ReplyNotReadable = Reply + "notreadable";
    String ReplyType = Reply + "type~";
    String ReplyNull = Reply + "null~";
    String ReplyArrayBegin = Reply + "array~begin";
    String ReplyArrayDone = Reply + "array~begin";
    String ReplyArrayIng = Reply + "";
    String ReplyLine1 = Reply + "line1~";
    String ReplyOk1 = Reply + "ok1";
    String ReplyFail1 = Reply + "fail1";
    String ReplyHandler = Reply + "handler";
    String ReplyHandlerException = ReplyHandler + "~exception";
    String SendHandler = Prefix + "send~handler";
}
