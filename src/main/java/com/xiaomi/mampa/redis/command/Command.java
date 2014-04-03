/**
 * Command.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 29, 2014 5:24:44 PM
 */

package com.xiaomi.mampa.redis.command;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;

import com.xiaomi.mampa.redis.codec.ICodec;
import com.xiaomi.mampa.redis.codec.IKeyValueCodec;
import com.xiaomi.mampa.redis.reply.Reply;
import com.xiaomi.mampa.redis.result.IResultReturner;
import com.xiaomi.mampa.redis.result.Result;
import com.xiaomi.mampa.redis.utils.Utils;

/**
 * Redis Command.
 * 
 * @author leo
 */
public class Command<K, V, R> {
    private final Type type;
    private final Args<K, V> args;
    private final Result<K, V, R> result;
    private final IResultReturner<R> returner;

    /**
     * Constructor.
     * 
     * @param type
     * @param args
     * @param result
     * @param returner
     */
    public Command(Type type, Args<K, V> args, Result<K, V, R> result, IResultReturner<R> returner) {
        this.type = type;
        this.result = result;
        this.returner = returner;
        this.args = args;
    }

    /**
     * Parse #raw from redis-server and <BR>
     * Returns this command's {@link #result} to caller using {@link #returner}.
     * 
     * @param raw
     */
    public void returns(Reply.Raw raw) {
        switch (raw.type()) {
            case Error: {
                returner.fail(result.setError(raw.error()).error());
                return;
            }
            case Array: {
                for (byte[] r : raw.results()) {
                    result.set(r);
                }
                returner.success(result.get());
                return;
            }
            case Simple: {
                returner.success(result.set(raw.result()).get());
                return;
            }
            case Int: {
                returner.success(result.set(raw.intResult()).get());
                return;
            }
            default: {
                returner.fail("Invalid result from server!");
                return;
            }
        }
    }

    /**
     * Encode and write this command to the supplied buffer using the new
     * <a href="http://redis.io/topics/protocol">Unified Request Protocol</a>.
     * 
     * @param buf Buffer to write to.
     */
    public void encode(ByteBuf buf) {
        buf.writeByte('*');
        Utils.writeLongAsString(buf, 1 + (args != null ? args.count() : 0));
        buf.writeBytes(Utils.CrLf);
        buf.writeByte('$');
        Utils.writeLongAsString(buf, type.bytes.length);
        buf.writeBytes(Utils.CrLf);
        buf.writeBytes(type.bytes);
        buf.writeBytes(Utils.CrLf);
        if (args != null) {
            buf.writeBytes(args.buffer());
        }
    }

    @Override
    public String toString() {
        return String.format("<Type: %s, args: %s>", type, args);
    }

    /**
     * Arguments for redis Command.
     * 
     * @param <K>
     * @param <V>
     * @author leo
     */
    public static class Args<K, V> {
        private static final int DEFAULT_BUF_SIZE = 64;
        private final IKeyValueCodec<K, V> codec;
        private ByteBuf buf;
        private int count;

        /**
         * Constructor.
         * 
         * @param codec
         */
        public Args(final IKeyValueCodec<K, V> codec) {
            this.codec = codec;
            this.buf = Utils.allocByteBuf(DEFAULT_BUF_SIZE);
        }

        /**
         * Constructor.
         * 
         * @param keyCodec
         * @param valueCodec
         */
        public Args(final ICodec<K> keyCodec, final ICodec<V> valueCodec) {
            this.codec = new IKeyValueCodec<K, V>() {
                @Override
                public ICodec<K> key() {
                    return keyCodec;
                }

                @Override
                public ICodec<V> value() {
                    return valueCodec;
                }
            };
        }

        public ByteBuf buffer() {
            return buf;
        }

        public int count() {
            return count;
        }

        public Args<K, V> addKey(K key) {
            return write(codec.key().encode(key));
        }

        public Args<K, V> addKeys(K... keys) {
            for (K key : keys) {
                addKey(key);
            }
            return this;
        }

        public Args<K, V> addKeys(List<K> keys) {
            for (K key : keys) {
                addKey(key);
            }
            return this;
        }

        public Args<K, V> addValue(V value) {
            return write(codec.value().encode(value));
        }

        public Args<K, V> addValues(V... values) {
            for (V value : values) {
                addValue(value);
            }
            return this;
        }

        public Args<K, V> addValues(List<V> values) {
            for (V value : values) {
                addValue(value);
            }
            return this;
        }

        public Args<K, V> add(Map<K, V> map) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                write(codec.key().encode(entry.getKey()));
                write(codec.value().encode(entry.getValue()));
            }
            return this;
        }

        public Args<K, V> add(String s) {
            return write(s);
        }

        public Args<K, V> add(long n) {
            return write(Long.toString(n));
        }

        public Args<K, V> add(double n) {
            return write(Double.toString(n));
        }

        public Args<K, V> add(byte[] value) {
            return write(value);
        }

        public Args<K, V> add(Keyword keyword) {
            return write(keyword.bytes);
        }

        public Args<K, V> add(Type type) {
            return write(type.bytes);
        }

        private Args<K, V> write(byte[] arg) {
            buf.writeByte((byte) '$');
            Utils.writeLongAsString(buf, arg.length);
            buf.writeBytes(Utils.CrLf);
            buf.writeBytes(arg);
            buf.writeBytes(Utils.CrLf);
            count++;
            return this;
        }

        private Args<K, V> write(String arg) {
            return write(Utils.encodeString(arg));
        }

        @Override
        public String toString() {
            return String.valueOf(count);
        }
    }

    /**
     * Redis Command Type. Not all implemented.
     * 
     * @author leo
     */
    public static enum Type {
        // Connection
        AUTH, ECHO, PING, QUIT, SELECT,

        // Server
        BGREWRITEAOF, BGSAVE, CLIENT, CONFIG, DBSIZE, DEBUG, FLUSHALL, FLUSHDB, INFO, LASTSAVE, MONITOR, SAVE, 
        SHUTDOWN, SLAVEOF, SLOWLOG, SYNC,

        // Keys
        DEL, DUMP, EXISTS, EXPIRE, EXPIREAT, KEYS, MIGRATE, MOVE, OBJECT, PERSIST, PEXPIRE, PEXPIREAT, PTTL, 
        RANDOMKEY, RENAME, RENAMENX, RESTORE, TTL, TYPE,

        // String
        APPEND, GET, GETEX, GETRANGE, GETSET, MGET, MSET, MSETNX, SET, SETEX, SETNX, SETRANGE, STRLEN,

        // Numeric
        DECR, DECRBY, INCR, INCRBY, INCRBYFLOAT,

        // List
        BLPOP, BRPOP, BRPOPLPUSH, LINDEX, LINSERT, LLEN, LPOP, LPUSH, LPUSHX, LRANGE, LRANGEEX, LREM, LSET, LTRIM, 
        RPOP, RPOPLPUSH, RPUSH, RPUSHX, SORT,

        // Hash
        HDEL, HEXISTS, HGET, HGETALL, HINCRBY, HINCRBYFLOAT, HKEYS, HLEN, HMGET, HMSET, HSET, HSETNX, HVALS,

        // Transaction
        DISCARD, EXEC, MULTI, UNWATCH, WATCH,

        // Pub/Sub
        PSUBSCRIBE, PUBLISH, PUNSUBSCRIBE, SUBSCRIBE, UNSUBSCRIBE,

        // Sets
        SADD, SCARD, SDIFF, SDIFFSTORE, SINTER, SINTERSTORE, SISMEMBER, SMEMBERS, SMOVE, SPOP, SRANDMEMBER, 
        SREM, SUNION, SUNIONSTORE,

        // Sorted Set
        ZADD, ZCARD, ZCOUNT, ZINCRBY, ZINTERSTORE, ZRANGE, ZRANGEBYSCORE, ZRANK, ZREM, ZREMRANGEBYRANK, 
        ZREMRANGEBYSCORE, ZREVRANGE, ZREVRANGEBYSCORE, ZREVRANK, ZSCORE, ZUNIONSTORE,

        // Scripting
        EVAL, EVALSHA, SCRIPT,

        // Bits
        BITCOUNT, BITOP, GETBIT, SETBIT;

        public byte[] bytes;

        private Type() {
            bytes = Utils.encodeAscii(name());
        }
    }

    /**
     * Keyword modifiers for redis commands.
     * 
     * @author leo
     */
    public static enum Keyword {
        AFTER, AGGREGATE, ALPHA, AND, ASC, BEFORE, BY, COUNT, DESC, ENCODING, FLUSH, GETNAME, IDLETIME, 
        KILL, LEN, LIMIT, LIST, LOAD, MAX, MIN, NO, NOSAVE, NOT, ONE, OR, REFCOUNT, RESET, RESETSTAT, 
        SETNAME, STORE, SUM, WEIGHTS, WITHSCORES, XOR;

        public byte[] bytes;

        private Keyword() {
            bytes = Utils.encodeAscii(name());
        }
    }

}
