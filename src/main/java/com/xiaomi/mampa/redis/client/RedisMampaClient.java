/**
 * RedisMampaClient.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 2:41:21 PM
 */

package com.xiaomi.mampa.redis.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringKeyValueCodec;
import com.xiaomi.mampa.redis.codec.StringValueCodec;
import com.xiaomi.mampa.redis.command.Command;
import com.xiaomi.mampa.redis.command.SendHandler;
import com.xiaomi.mampa.redis.command.Command.Args;
import com.xiaomi.mampa.redis.command.Command.Keyword;
import com.xiaomi.mampa.redis.reply.Reply;
import com.xiaomi.mampa.redis.reply.ReplyHandler;
import com.xiaomi.mampa.redis.result.BooleanResult;
import com.xiaomi.mampa.redis.result.IResultReturner;
import com.xiaomi.mampa.redis.result.IntegerResult;
import com.xiaomi.mampa.redis.result.ListResult;
import com.xiaomi.mampa.redis.result.StatusResult;
import com.xiaomi.mampa.redis.result.ValueResult;


import static com.xiaomi.mampa.redis.command.Command.Type.DEL;
import static com.xiaomi.mampa.redis.command.Command.Type.EXISTS;
import static com.xiaomi.mampa.redis.command.Command.Type.EXPIRE;
import static com.xiaomi.mampa.redis.command.Command.Type.FLUSHALL;
import static com.xiaomi.mampa.redis.command.Command.Type.FLUSHDB;
import static com.xiaomi.mampa.redis.command.Command.Type.GET;
import static com.xiaomi.mampa.redis.command.Command.Type.GETEX;
import static com.xiaomi.mampa.redis.command.Command.Type.LINDEX;
import static com.xiaomi.mampa.redis.command.Command.Type.LINSERT;
import static com.xiaomi.mampa.redis.command.Command.Type.LLEN;
import static com.xiaomi.mampa.redis.command.Command.Type.LPOP;
import static com.xiaomi.mampa.redis.command.Command.Type.LPUSH;
import static com.xiaomi.mampa.redis.command.Command.Type.LPUSHX;
import static com.xiaomi.mampa.redis.command.Command.Type.LRANGE;
import static com.xiaomi.mampa.redis.command.Command.Type.LRANGEEX;
import static com.xiaomi.mampa.redis.command.Command.Type.LREM;
import static com.xiaomi.mampa.redis.command.Command.Type.LSET;
import static com.xiaomi.mampa.redis.command.Command.Type.LTRIM;
import static com.xiaomi.mampa.redis.command.Command.Type.PING;
import static com.xiaomi.mampa.redis.command.Command.Type.RPOP;
import static com.xiaomi.mampa.redis.command.Command.Type.RPOPLPUSH;
import static com.xiaomi.mampa.redis.command.Command.Type.RPUSH;
import static com.xiaomi.mampa.redis.command.Command.Type.RPUSHX;
import static com.xiaomi.mampa.redis.command.Command.Type.SET;
import static com.xiaomi.mampa.redis.command.Command.Type.SETEX;
import static com.xiaomi.mampa.redis.command.Command.Type.SETNX;
import static com.xiaomi.mampa.redis.command.Command.Type.TTL;
import static com.xiaomi.mampa.redis.command.Command.Type.TYPE;

/**
 * {@link IClient} using mampa.
 * 
 * @author leo
 */
public class RedisMampaClient implements IClient {
    private static final Logger logger = LoggerFactory.getLogger(RedisMampaClient.class);
    private final String host;
    private final int port;
    private Channel channel;
    private EventLoopGroup group;
    private RedisActor actor;
    private AtomicBoolean started = new AtomicBoolean(false);

    public RedisMampaClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup(2);
        this.actor = new RedisActor();
        start();
    }

    public synchronized RedisMampaClient start() {
        if (started.get()) {
            return this;
        }
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new SendHandler(), new ReplyHandler(RedisMampaClient.this));
                }
            });
            // Start the client.
            channel = b.connect(host, port).syncUninterruptibly().channel();
            logger.info("Connected to redis-server {}:{}...", host, port);
        } catch (Exception ex) {
            group.shutdownGracefully();
            actor.shutdown();
            logger.error("Connect to redis-server " + host + ":" + port + " got exception!", ex);
            return null;
        }
        actor.start(channel);
        started.set(true);
        return this;
    }

    public void shutdown() {
        if (channel != null) {
            try {
                channel.flush();
                logger.info("Shutdown NettyBooter Client for {}:{}..", host, port);
                channel.close().syncUninterruptibly();
            } finally {
                group.shutdownGracefully();
                channel = null;
            }
        }
        actor.shutdown();
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    private boolean send(Command<?, ?, ?> command) {
        return actor.send(command);
    }

    public <K, V> boolean received(Reply.Raw raw) throws InterruptedException {
        return actor.recv(raw);
    }

    public void await() {
        if (channel != null) {
            channel.closeFuture().syncUninterruptibly();
        }
    }

    @Override
    public boolean ping(IResultReturner<String> returner) {
        return send(new Command<String, String, String>(PING, null, new ValueResult<String, String>(
                StringKeyValueCodec.instance()), returner));
    }

    @Override
    public <K> boolean type(K key, StringValueCodec<K> codec, IResultReturner<String> returner) {
        return send(new Command<K, String, String>(TYPE, new Args<K, String>(codec).addKey(key),
                new ValueResult<K, String>(codec), returner));
    }

    @Override
    public <K, V> boolean exists(K key, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(EXISTS, new Args<K, V>(codec).addKey(key),
                new BooleanResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean expire(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(EXPIRE, new Args<K, V>(codec).addKey(key).add(seconds),
                new BooleanResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean get(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(GET, new Args<K, V>(codec).addKey(key), new ValueResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean getex(K key, int seconds, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(GETEX, new Args<K, V>(codec).addKey(key).add(seconds), new ValueResult<K, V>(
                codec), returner));
    }

    @Override
    public <K, V> boolean set(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(SET, new Args<K, V>(codec).addKey(key).addValue(value),
                new StatusResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean setex(K key, int seconds, V value, IKeyValueCodec<K, V> codec,
            IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(SETEX, new Args<K, V>(codec).addKey(key).add(seconds).addValue(value),
                new StatusResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean setnx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(SETNX, new Args<K, V>(codec).addKey(key).addValue(value),
                new BooleanResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean ttl(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(TTL, new Args<K, V>(codec).addKey(key), new IntegerResult<K, V>(codec),
                returner));
    }

    @Override
    public<K, V> boolean lindex(K key, int index, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(LINDEX, new Args<K, V>(codec).addKey(key).add(index),
                new ValueResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean linsert(K key, boolean before, V pivot, V value, IKeyValueCodec<K, V> codec,
            IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LINSERT, new Args<K, V>(codec).addKey(key)
                .add(before ? Keyword.BEFORE : Keyword.AFTER).addValue(pivot).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean llen(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LLEN, new Args<K, V>(codec).addKey(key), new IntegerResult<K, V>(codec),
                returner));
    }

    @Override
    public <K, V> boolean lpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(LPOP, new Args<K, V>(codec).addKey(key), new ValueResult<K, V>(codec),
                returner));
    }

    @Override
    public <K, V> boolean lpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LPUSH, new Args<K, V>(codec).addKey(key).addValues(values),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LPUSH, new Args<K, V>(codec).addKey(key).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LPUSHX, new Args<K, V>(codec).addKey(key).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lrange(K key, int start, int stop, IKeyValueCodec<K, V> codec,
            IResultReturner<List<V>> returner) {
        return send(new Command<K, V, List<V>>(LRANGE, new Args<K, V>(codec).addKey(key).add(start).add(stop),
                new ListResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lrangeex(K key, int start, int stop, int seconds, IKeyValueCodec<K, V> codec,
            IResultReturner<List<V>> returner) {
        return send(new Command<K, V, List<V>>(LRANGEEX, new Args<K, V>(codec).addKey(key).add(start).add(stop).add(seconds),
                new ListResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lrem(K key, int count, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(LREM, new Args<K, V>(codec).addKey(key).add(count).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean lset(K key, int index, V value, IKeyValueCodec<K, V> codec, IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(LSET, new Args<K, V>(codec).addKey(key).add(index).addValue(value),
                new StatusResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean ltrim(K key, int start, int stop, IKeyValueCodec<K, V> codec,
            IResultReturner<Boolean> returner) {
        return send(new Command<K, V, Boolean>(LTRIM, new Args<K, V>(codec).addKey(key).add(start).add(stop),
                new StatusResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean rpop(K key, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(RPOP, new Args<K, V>(codec).addKey(key), new ValueResult<K, V>(codec),
                returner));
    }

    @Override
    public <K, V> boolean rpoplpush(K src, K dest, IKeyValueCodec<K, V> codec, IResultReturner<V> returner) {
        return send(new Command<K, V, V>(RPOPLPUSH, new Args<K, V>(codec).addKey(src).addKey(dest), new ValueResult<K, V>(codec),
                returner));
    }

    @Override
    public <K, V> boolean rpush(K key, List<V> values, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(RPUSH, new Args<K, V>(codec).addKey(key).addValues(values),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean rpush(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(RPUSH, new Args<K, V>(codec).addKey(key).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean rpushx(K key, V value, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(RPUSHX, new Args<K, V>(codec).addKey(key).addValue(value),
                new IntegerResult<K, V>(codec), returner));
    }
    
    @Override
    public <K, V> boolean del(K key, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(DEL, new Args<K, V>(codec).addKey(key),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public <K, V> boolean del(List<K> keys, IKeyValueCodec<K, V> codec, IResultReturner<Integer> returner) {
        return send(new Command<K, V, Integer>(DEL, new Args<K, V>(codec).addKeys(keys),
                new IntegerResult<K, V>(codec), returner));
    }

    @Override
    public boolean flushall(IResultReturner<Boolean> returner) {
        return send(new Command<String, String, Boolean>(FLUSHALL, null, new StatusResult<String, String>(
                StringKeyValueCodec.instance()), returner));
    }

    @Override
    public boolean flushdb(IResultReturner<Boolean> returner) {
        return send(new Command<String, String, Boolean>(FLUSHDB, null, new StatusResult<String, String>(
                StringKeyValueCodec.instance()), returner));
    }

}
