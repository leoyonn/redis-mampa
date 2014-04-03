/**
 * ResultFsm.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 4:19:42 PM
 */
package com.xiaomi.mampa.redis.reply;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;

import com.xiaomi.mampa.redis.client.RedisMampaClient;
import com.xiaomi.mampa.redis.utils.PerfConstants;
import com.xiaomi.oms.perf.Perf;

/**
 * A small fsm collecting data from netty-channel connected to redis-server.
 * 
 * @author leo
 */
public class ReplyFsm {
    private static class State {
        private Reply.Type type;
        private int count = -1;
    }

    private LinkedList<State> queue;
    private RedisMampaClient master;
    private Reply.Raw raw;

    public ReplyFsm(RedisMampaClient master) {
        this.master = master;
        this.queue = new LinkedList<State>();
        this.raw = new Reply.Raw();
    }

    /**
     * Attempt to decode a redis response and return a flag indicating whether a complete response was read.
     * 
     * @param buffer Buffer containing data from the server.
     * @param output Current command output.
     * @return true if a complete response was read.
     * @throws InterruptedException
     */
    public boolean read1(ByteBuf buf) throws InterruptedException {
        long begin = Perf.begin();
        Perf.count(PerfConstants.ReplyBegin);
        int length, end;
        byte[] bytes;

        if (queue.isEmpty()) {
            queue.add(new State());
        }

        loop: while (!queue.isEmpty()) {
            // 1. Read reply type
            State state = queue.peek();
            if (state.type == null) {
                if (!buf.isReadable()) {
                    Perf.count(PerfConstants.ReplyNotReadable);
                    break;
                }
                state.type = Reply.type(buf.readByte());
                Perf.count(PerfConstants.ReplyType + state.type);
                buf.markReaderIndex();
            }

            // 2. Read content
            switch (state.type) {
            case Simple: {
                if ((bytes = Reply.readLine(buf)) == null) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    break loop;
                }
                raw.result(bytes);
                break;
            }
            case Error: {
                if ((bytes = Reply.readLine(buf)) == null) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    break loop;
                }
                raw.error(bytes);
                break;
            }
            case Int: {
                if ((end = Reply.findLineEnd(buf)) == -1) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    break loop;
                }
                int v = (int) Reply.readLong(buf, buf.readerIndex(), end);
                raw.result(v);
                break;
            }
            case Bulk: {
                if ((end = Reply.findLineEnd(buf)) == -1) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    break loop;
                }
                length = (int) Reply.readLong(buf, buf.readerIndex(), end);
                if (length == -1) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    raw.result(null);
                } else {
                    state.type = Reply.Type.Bytes;
                    state.count = length + 2;
                    buf.markReaderIndex();
                    continue loop;
                }
                break;
            }
            case Array: {
                if (state.count == -1) {
                    if ((end = Reply.findLineEnd(buf)) == -1) {
                        Perf.count(PerfConstants.ReplyNull + state.type);
                        break loop;
                    }
                    length = (int) Reply.readLong(buf, buf.readerIndex(), end);
                    state.count = length;
                    buf.markReaderIndex();
                    raw.arrayBegin();
                    Perf.count(PerfConstants.ReplyArrayBegin);
                }

                if (state.count <= 0) {
                    Perf.count(PerfConstants.ReplyArrayDone);
                    raw.arrayDone();
                    break;
                }

                Perf.count(PerfConstants.ReplyArrayIng + state.type);
                state.count--;
                queue.addFirst(new State());
                continue loop;
            }
            case Bytes: {
                if ((bytes = Reply.readBytes(buf, state.count)) == null) {
                    Perf.count(PerfConstants.ReplyNull + state.type);
                    break loop;
                }
                raw.result(bytes);
                break;
            }
            default:
                break;
            }

            buf.markReaderIndex();
            queue.remove();
            Perf.elapse(PerfConstants.ReplyLine1 + state.type, begin);
        }
        boolean done = queue.isEmpty();
        if (done) {
            master.received(raw);
            Perf.elapse(PerfConstants.ReplyOk1, begin);
            raw = new Reply.Raw();
        } else {
            Perf.elapse(PerfConstants.ReplyFail1, begin);
        }
        return done;
    }
}
