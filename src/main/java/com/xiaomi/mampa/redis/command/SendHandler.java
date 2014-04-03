/**
 * SendHandler.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 28, 2014 10:27:36 AM
 */
package com.xiaomi.mampa.redis.command;

import com.xiaomi.mampa.redis.utils.PerfConstants;
import com.xiaomi.oms.perf.Perf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * Netty channel handler for encoding {@link Command} to {@link ByteBuf}.
 * 
 * @author leo
 */
public class SendHandler extends MessageToByteEncoder<Command<?, ?, ?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Command<?, ?, ?> msg, ByteBuf out) throws Exception {
        long begin = Perf.begin();
        msg.encode(out);
        Perf.elapse(PerfConstants.SendHandler, begin);
    }
}
