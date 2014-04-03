/**
 * RedisMampaTest.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 27, 2014 9:28:26 AM
 */
package com.xiaomi.mampa.redis;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xiaomi.mampa.redis.client.ClientFactory;
import com.xiaomi.mampa.redis.client.IClient;
import com.xiaomi.mampa.redis.codec.StringKeyValueCodec;
import com.xiaomi.mampa.redis.mock.DummyRedis;
import com.xiaomi.mampa.redis.result.IResultReturner;
import com.xiaomi.mampa.redis.utils.Utils;

/**
 * @author leo
 */
public class ClientTest {
    IClient client;
    StringKeyValueCodec codec = StringKeyValueCodec.instance();

    Object r = null;
    Object fail = null;
    IResultReturner<Boolean> boolReturner = new IResultReturner<Boolean>() {
        @Override
        public void success(Boolean result) {
            r = result;
            fail = null;
        }

        @Override
        public void fail(String message) {
            r = null;
            fail = message;
        }
    };

    IResultReturner<Integer> intReturner = new IResultReturner<Integer>() {
        @Override
        public void success(Integer result) {
            r = result;
            fail = null;
        }

        @Override
        public void fail(String message) {
            r = null;
            fail = message;
        }
    };

    IResultReturner<String> valueReturner = new IResultReturner<String>() {
        @Override
        public void success(String result) {
            r = result;
            fail = null;
        }

        @Override
        public void fail(String message) {
            r = null;
            fail = message;
        }
    };

    IResultReturner<List<String>> valuesReturner = new IResultReturner<List<String>>() {
        @Override
        public void success(List<String> result) {
            r = result;
            fail = null;
        }

        @Override
        public void fail(String message) {
            r = null;
            fail = message;
        }
    };

    void print(String fmt, Object... objs) {
        System.out.println(String.format(fmt, objs));
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    void asserts(Object expect) throws InterruptedException {
        Thread.sleep(100);
        Assert.assertEquals(expect, r);
    }

    void assertFail() throws InterruptedException {
        Thread.sleep(100);
        Assert.assertNull(r);
        Assert.assertNotNull(fail);
        print(">>>> Fail string: " + fail);
    }

    @Test
    public void testClient() throws InterruptedException {
        client = ClientFactory.getLocalClient();
        Assert.assertNotNull(client);
        client.ping(valueReturner);
        asserts("PONG");
        client = ClientFactory.getLocalClient(6379);
        Assert.assertNotNull(client);
        client.ping(valueReturner);
        asserts("PONG");
        client = ClientFactory.get("localhost", 6379);
        Assert.assertNotNull(client);
        client.ping(valueReturner);
        asserts("PONG");
        // flush/ping/type..
        client.flushall(boolReturner);
        asserts(true);
        client.ping(valueReturner);
        asserts("PONG");
        client.exists("x", codec, boolReturner);
        asserts(false);
        client.type("x", codec, valueReturner);
        asserts("none");
        client.set("x", "字符串1", codec, boolReturner);
        asserts(true);
        client.exists("x", codec, boolReturner);
        asserts(true);
        client.get("x", codec, valueReturner);
        asserts("字符串1");
        client.type("x", codec, valueReturner);
        asserts("string");

        // get/getex/set/setex/ttl...
        client.getex("x", 10, codec, valueReturner);
        asserts("字符串1");
        client.ttl("x", codec, intReturner);
        asserts(10);
        client.getex("x", 0, codec, valueReturner);
        asserts("字符串1");
        client.ttl("x", codec, intReturner);
        asserts(10);
        client.getex("x", -1, codec, valueReturner);
        asserts("字符串1");
        client.ttl("x", codec, intReturner);
        asserts(-2);
        client.getex("x", 10, codec, valueReturner);
        asserts(null);
        client.ttl("x", codec, intReturner);
        asserts(-2);
        client.setex("x", 1, "字符串2", codec, boolReturner);
        asserts(true);
        client.get("x", codec, valueReturner);
        asserts("字符串2");
        Thread.sleep(2000);
        client.get("x", codec, valueReturner);
        asserts(null);
        client.setnx("y", "字符串3", codec, boolReturner);
        asserts(true);
        client.get("y", codec, valueReturner);
        asserts("字符串3");
        client.setnx("y", "字符串4", codec, boolReturner);
        asserts(false);
        client.get("y", codec, valueReturner);
        asserts("字符串3");
        client.set("x", "字符串1", codec, boolReturner);
        asserts(true);
        client.set("y", "字符串2", codec, boolReturner);
        asserts(true);
        client.set("z", "字符串3", codec, boolReturner);
        asserts(true);
        client.get("x", codec, valueReturner);
        asserts("字符串1");
        client.del("x", codec, intReturner);
        asserts(1);
        client.get("x", codec, valueReturner);
        asserts(null);
        client.get("y", codec, valueReturner);
        asserts("字符串2");
        client.get("z", codec, valueReturner);
        asserts("字符串3");
        client.del(Arrays.asList(new String[]{"x", "y", "z"}), codec, intReturner);
        asserts(2);
        client.get("y", codec, valueReturner);
        asserts(null);
        client.get("z", codec, valueReturner);
        asserts(null);
        client.set("x", "字符串1", codec, boolReturner);
        asserts(true);

        // lpush/lpop/lrange/lrange/ltrim...
        client.lpush("x", "字符串11", codec, intReturner);
        assertFail();
        client.lindex("x", 0, codec, valueReturner);
        assertFail();
        client.lpush("z", "字符串l1", codec, intReturner);
        asserts(1);
        client.lpushx("x", "字符串l1", codec, intReturner);
        assertFail();
        client.lpushx("y", "字符串l1", codec, intReturner);
        asserts(0);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l1"}));
        client.lpush("z", Arrays.asList(new String[]{"字符串l2", "字符串l3", "字符串l4"}), codec, intReturner);
        asserts(4);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l4", "字符串l3", "字符串l2", "字符串l1"}));
        client.lrange("z", 1, 10, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l3", "字符串l2", "字符串l1"}));
        client.lrange("z", 1, 2, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l3", "字符串l2"}));
        client.lrange("z", 10, 2, codec, valuesReturner);
        asserts(Collections.emptyList());
        client.lindex("z", -1, codec, valueReturner);
        asserts("字符串l1");
        client.linsert("z", true, "字符串l3", "字符串l3b", codec, intReturner);
        asserts(5);
        client.linsert("z", false, "字符串l3", "字符串l3a", codec, intReturner);
        asserts(6);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l4", "字符串l3b", "字符串l3", "字符串l3a", "字符串l2", "字符串l1"}));
        client.llen("z", codec, intReturner);
        asserts(6);
        client.lpop("z", codec, valueReturner);
        asserts("字符串l4");
        client.lpop("z", codec, valueReturner);
        asserts("字符串l3b");
        client.lpop("z", codec, valueReturner);
        asserts("字符串l3");
        client.lpush("z", "字符串l3a", codec, intReturner);
        asserts(4);
        client.rpush("z", "字符串l3a", codec, intReturner);
        asserts(5);
        client.rpushx("z", "字符串l3a", codec, intReturner);
        asserts(6);
        client.lrange("z", 0, 10, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l3a", "字符串l3a", "字符串l2", "字符串l1", "字符串l3a", "字符串l3a"}));
        client.rpushx("z1", "字符串l3a", codec, intReturner);
        asserts(0);
        client.rpushx("x", "字符串l3a", codec, intReturner);
        assertFail();
        client.lrem("z", -1, "字符串l3a", codec, intReturner);
        asserts(1);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l3a", "字符串l3a", "字符串l2", "字符串l1", "字符串l3a"}));
        client.lrem("z", 2, "字符串l3a", codec, intReturner);
        asserts(2);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l2", "字符串l1", "字符串l3a"}));
        client.lrem("z", 0, "字符串l3a", codec, intReturner);
        asserts(1);
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l2", "字符串l1"}));
        client.lpush("z", Arrays.asList(new String[]{"字符串l3", "字符串l4", "字符串l5"}), codec, intReturner);
        asserts(5);
        client.rpoplpush("z", "z1", codec, valueReturner);
        asserts("字符串l1");
        client.lrange("z", 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l5", "字符串l4", "字符串l3", "字符串l2"}));
        client.lset("z", 1, "字符串l3", codec, boolReturner);
        asserts(true);
        client.lset("z", -2, "字符串l2", codec, boolReturner);
        asserts(true);
        client.lset("z", 10, "字符串l2", codec, boolReturner);
        assertFail();
        client.ltrim("z", 1, -2, codec, boolReturner);
        asserts(true);
        client.lpush("z", "字符串l4", codec, intReturner);
        asserts(3);
        client.rpush("z", "字符串l1", codec, intReturner);
        asserts(4);

        // lrangeex/del
        client.ttl("z", codec, intReturner);
        asserts(-1);
        client.lrangeex("z", 0, -1, 10, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l4", "字符串l3", "字符串l2", "字符串l1"}));
        client.ttl("z", codec, intReturner);
        asserts(10);
        client.lrangeex("z", -2, -1, 0, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l2", "字符串l1"}));
        client.ttl("z", codec, intReturner);
        asserts(10);
        client.lrangeex("z", 0, 0, -1, codec, valuesReturner);
        asserts(Arrays.asList(new String[]{"字符串l4"}));
        client.ttl("z", codec, intReturner);
        asserts(-2);
        client.lrangeex("z", 0, -1, 10, codec, valuesReturner);
        asserts(Collections.emptyList());
        client.ttl("z", codec, intReturner);
        asserts(-2);
        client.del(Arrays.asList(new String[]{"x", "y", "z", "z1"}), codec, intReturner);
        asserts(2);
        client.get("z1", codec, valueReturner);
        asserts(null);
        client.rpoplpush("z", "z1", codec, valueReturner);
        asserts(null);

        client.flushdb(boolReturner);
        asserts(true);
        client.get("z1", codec, valueReturner);
        asserts(null);
        Thread.sleep(1000);
    }

    @Test
    public void testDummy() throws InterruptedException {
        // flush/ping/type..
        client = new DummyRedis();
        client.flushall(boolReturner);
        client.ping(valueReturner);
        client.exists("x", codec, boolReturner);
        client.type("x", codec, valueReturner);
        client.set("x", "字符串1", codec, boolReturner);
        client.exists("x", codec, boolReturner);
        client.get("x", codec, valueReturner);
        client.type("x", codec, valueReturner);

        // get/getex/set/setex/ttl...
        client.getex("x", 10, codec, valueReturner);
        client.ttl("x", codec, intReturner);
        client.getex("x", 0, codec, valueReturner);
        client.ttl("x", codec, intReturner);
        client.getex("x", -1, codec, valueReturner);
        client.ttl("x", codec, intReturner);
        client.getex("x", 10, codec, valueReturner);
        client.ttl("x", codec, intReturner);
        client.setex("x", 1, "字符串2", codec, boolReturner);
        client.get("x", codec, valueReturner);
        client.get("x", codec, valueReturner);
        client.setnx("y", "字符串3", codec, boolReturner);
        client.get("y", codec, valueReturner);
        client.setnx("y", "字符串4", codec, boolReturner);
        client.get("y", codec, valueReturner);
        client.set("x", "字符串1", codec, boolReturner);
        client.set("y", "字符串2", codec, boolReturner);
        client.set("z", "字符串3", codec, boolReturner);
        client.get("x", codec, valueReturner);
        client.del("x", codec, intReturner);
        client.get("x", codec, valueReturner);
        client.get("y", codec, valueReturner);
        client.get("z", codec, valueReturner);
        client.del(Arrays.asList(new String[] {
                "x", "y", "z"
        }), codec, intReturner);
        client.get("y", codec, valueReturner);
        client.get("z", codec, valueReturner);
        client.set("x", "字符串1", codec, boolReturner);

        // lpush/lpop/lrange/lrange/ltrim...
        client.lpush("x", "字符串11", codec, intReturner);
        client.lindex("x", 0, codec, valueReturner);
        client.lpush("z", "字符串l1", codec, intReturner);
        client.lpushx("x", "字符串l1", codec, intReturner);
        client.lpushx("y", "字符串l1", codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lpush("z", Arrays.asList(new String[] {
                "字符串l2", "字符串l3", "字符串l4"
        }), codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lrange("z", 1, 10, codec, valuesReturner);
        client.lrange("z", 1, 2, codec, valuesReturner);
        client.lrange("z", 10, 2, codec, valuesReturner);
        client.lindex("z", -1, codec, valueReturner);
        client.linsert("z", true, "字符串l3", "字符串l3b", codec, intReturner);
        client.linsert("z", false, "字符串l3", "字符串l3a", codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.llen("z", codec, intReturner);
        client.lpop("z", codec, valueReturner);
        client.lpop("z", codec, valueReturner);
        client.lpop("z", codec, valueReturner);
        client.lpush("z", "字符串l3a", codec, intReturner);
        client.rpush("z", "字符串l3a", codec, intReturner);
        client.rpushx("z", "字符串l3a", codec, intReturner);
        client.lrange("z", 0, 10, codec, valuesReturner);
        client.rpushx("z1", "字符串l3a", codec, intReturner);
        client.rpushx("x", "字符串l3a", codec, intReturner);
        client.lrem("z", -1, "字符串l3a", codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lrem("z", 2, "字符串l3a", codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lrem("z", 0, "字符串l3a", codec, intReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lpush("z", Arrays.asList(new String[] {
                "字符串l3", "字符串l4", "字符串l5"
        }), codec, intReturner);
        client.rpoplpush("z", "z1", codec, valueReturner);
        client.lrange("z", 0, -1, codec, valuesReturner);
        client.lset("z", 1, "字符串l3", codec, boolReturner);
        client.lset("z", -2, "字符串l2", codec, boolReturner);
        client.lset("z", 10, "字符串l2", codec, boolReturner);
        client.ltrim("z", 1, -2, codec, boolReturner);
        client.lpush("z", "字符串l4", codec, intReturner);
        client.rpush("z", "字符串l1", codec, intReturner);

        // lrangeex/del
        client.ttl("z", codec, intReturner);
        client.lrangeex("z", 0, -1, 10, codec, valuesReturner);
        client.ttl("z", codec, intReturner);
        client.lrangeex("z", -2, -1, 0, codec, valuesReturner);
        client.ttl("z", codec, intReturner);
        client.lrangeex("z", 0, 0, -1, codec, valuesReturner);
        client.ttl("z", codec, intReturner);
        client.lrangeex("z", 0, -1, 10, codec, valuesReturner);
        client.ttl("z", codec, intReturner);
        client.del(Arrays.asList(new String[] {
                "x", "y", "z", "z1"
        }), codec, intReturner);
        client.get("z1", codec, valueReturner);
        client.rpoplpush("z", "z1", codec, valueReturner);

        client.flushdb(boolReturner);
        client.get("z1", codec, valueReturner);
        Thread.sleep(1000);
    }

    @Test
    public void testUtils() {
        ByteBuf buf = Utils.allocByteBuf(10);
        Utils.writeLongAsString(buf, 1234567);
        System.out.println(buf);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        Assert.assertEquals("1234567", new String(bytes));
        Utils.writeLongAsStringV2(buf, 1234567);
        System.out.println(buf);
        bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes);
        Assert.assertEquals("12345671234567", new String(bytes));
    }
    
    // @Test
    // public void testSendRaw() {
    // RedisMampaClient client = (RedisMampaClient) this.client;
    // client.sendRaw("PING\r\n");
    // client.sendRaw("*3\r\n$3\r\nSET\r\n$1\r\nx\r\n$4\r\n1111\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\nx\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*4\r\n$5\r\nSETEX\r\n$1\r\nx\r\n$2\r\n99\r\n$4\r\n2222\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\nx\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*4\r\n$5\r\nSETEX\r\n$1\r\nx\r\n$2\r\n00\r\n$4\r\n3333\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\nx\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*4\r\n$5\r\nSETEX\r\n$1\r\nx\r\n$2\r\n-1\r\n$4\r\n4444\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\nx\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*4\r\n$5\r\nSETEX\r\n$1\r\nx\r\n$2\r\n-2\r\n$4\r\n5555\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\nx\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*3\r\n$5\r\nGETEX\r\n$1\r\nx\r\n$2\r\n19\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*3\r\n$5\r\nGETEX\r\n$1\r\nx\r\n$2\r\n00\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*3\r\n$5\r\nGETEX\r\n$1\r\nx\r\n$2\r\n-1\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*3\r\n$5\r\nGETEX\r\n$1\r\nx\r\n$2\r\n-2\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\nx\r\n");
    // client.sendRaw("*5\r\n$5\r\nLPUSH\r\n$1\r\ny\r\n$3\r\n111\r\n$3\r\n222\r\n$3\r\n333\r\n");
    // client.sendRaw("*2\r\n$3\r\nGET\r\n$1\r\ny\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // client.sendRaw("*4\r\n$6\r\nLRANGE\r\n$1\r\ny\r\n$1\r\n0\r\n$2\r\n-1\r\n");
    // client.sendRaw("*3\r\n$6\r\nEXPIRE\r\n$1\r\ny\r\n$2\r\n99\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // client.sendRaw("*5\r\n$8\r\nLRANGEEX\r\n$1\r\ny\r\n$1\r\n0\r\n$2\r\n-1\r\n$2\r\n29\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // client.sendRaw("*5\r\n$8\r\nLRANGEEX\r\n$1\r\ny\r\n$1\r\n0\r\n$2\r\n-1\r\n$2\r\n00\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // client.sendRaw("*5\r\n$8\r\nLRANGEEX\r\n$1\r\ny\r\n$1\r\n0\r\n$2\r\n-1\r\n$2\r\n-1\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // client.sendRaw("*5\r\n$8\r\nLRANGEEX\r\n$1\r\ny\r\n$1\r\n0\r\n$2\r\n-1\r\n$2\r\n-2\r\n");
    // client.sendRaw("*2\r\n$3\r\nTTL\r\n$1\r\ny\r\n");
    // }

}
