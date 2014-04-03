/**
 * ReplyHandler.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 2:52:33 PM
 */
package com.xiaomi.mampa.redis.reply;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaomi.mampa.redis.client.RedisMampaClient;
import com.xiaomi.mampa.redis.utils.PerfConstants;
import com.xiaomi.oms.perf.Perf;

/**
 * Netty channel handler for reply from redis-server.
 * 
 * @author leo
 */
public class ReplyHandler extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ReplyHandler.class);
    private ReplyFsm fsm;

    public ReplyHandler(RedisMampaClient master) {
        fsm = new ReplyFsm(master);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        long begin = Perf.begin();
        try {
            while (fsm.read1(in));
        } catch (Exception ex) {
            Perf.elapse(PerfConstants.ReplyHandlerException, begin);
            Perf.count(PerfConstants.ReplyHandlerException + ex.getClass().getSimpleName());
            logger.warn("Decode command got exception", ex);
        }
        Perf.elapse(PerfConstants.ReplyHandler, begin);
    }
}
