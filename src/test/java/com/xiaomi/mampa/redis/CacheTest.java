/**
 * CacheTest.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 2, 2014 5:49:03 PM
 */

package com.xiaomi.mampa.redis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.xiaomi.mampa.redis.cache.ICache;
import com.xiaomi.mampa.redis.cache.ThriftCache;
import com.xiaomi.mampa.redis.result.IResultReturner;
import com.xiaomi.mampa.redis.test.thrift.AppUser;
import com.xiaomi.oms.perf.Perf;

/**
 * Test cases and using-demo for ThriftCache and BaseCache.
 * 
 * @author leo
 */
public class CacheTest {
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

    IResultReturner<AppUser> valueReturner = new IResultReturner<AppUser>() {
        @Override
        public void success(AppUser result) {
            r = result;
            fail = null;
        }

        @Override
        public void fail(String message) {
            r = null;
            fail = message;
        }
    };

    IResultReturner<List<AppUser>> valuesReturner = new IResultReturner<List<AppUser>>() {
        @Override
        public void success(List<AppUser> result) {
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
    public void testThriftCache() throws InterruptedException {
        String key1 = "x", key2 = "x. asdf xxxxxxxxxxxx", key3 = "xxxxxxxx这是个随意的keyxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        AppUser user0 = new AppUser("5000", "xiaomi.com", 1001L),
                user1 = new AppUser("5001", "xiaomi.com", 1001L),
                user2 = new AppUser("5002", "xiaomi.com", 1001L),
                user3 = new AppUser("5003", "xiaomi.com", 1001L),
                user4 = new AppUser("5004", "xiaomi.com", 1001L);

        ICache<String, AppUser> cache = new ThriftCache<AppUser>(AppUser.class);
        Assert.assertNotNull(cache.client());
        Assert.assertNotNull(cache.codec());
        cache.deleteAllData(boolReturner);
        asserts(true);

        cache.ping(boolReturner);
        asserts(true);
        cache.set(key1, user1, boolReturner);
        asserts(true);
        cache.get(key1, valueReturner);
        asserts(user1);
        cache.exists(key1, boolReturner);
        asserts(true);
        cache.elements(key1, valuesReturner);
        assertFail();
        cache.getAndUpdateTtl(key1, 10, valueReturner);
        asserts(user1);
        cache.ttl(key1, intReturner);
        asserts(10);
        cache.getAndUpdateTtl(key1, 0, valueReturner);
        asserts(user1);
        cache.ttl(key1, intReturner);
        asserts(10);
        cache.getAndUpdateTtl(key1, -1, valueReturner);
        asserts(user1);
        cache.ttl(key1, intReturner);
        asserts(-2);
        cache.getAndUpdateTtl(key1, 10, valueReturner);
        asserts(null);
        cache.setIfNotExists(key1, user1, boolReturner);
        asserts(true);
        cache.del(key1, intReturner);
        asserts(1);
        cache.set(key1, user1, boolReturner);
        asserts(true);
        cache.setIfNotExists(key1, user2, boolReturner);
        asserts(false);
        cache.get(key1, valueReturner);
        asserts(user1);
        cache.setIfNotExists(key2, user2, boolReturner);
        asserts(true);
        cache.get(key2, valueReturner);
        asserts(user2);
        cache.del(Arrays.asList(new String[]{key1, key2, key3}), intReturner);
        asserts(2);
        cache.offer(key3, user2, intReturner);
        asserts(1);
        cache.offerHeadIfExists(key3, user3, intReturner);
        asserts(2);
        cache.offerIfExists(key1, user2, intReturner);
        asserts(0);
        cache.offerHeadIfExists(key1, user3, intReturner);
        asserts(0);
        cache.offer(key3, Arrays.asList(new AppUser[]{user1, user1}), intReturner);
        asserts(4);
        cache.offerHead(key3, Arrays.asList(new AppUser[]{user4, user4}), intReturner);
        asserts(6);
        cache.elementAt(key3, 2, valueReturner);
        asserts(user3);
        cache.elements(key3, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user4, user4, user3, user2, user1, user1}));
        cache.insert(key3, true, user1, user0, intReturner); // 4432011
        asserts(7);
        cache.insert(key3, true, user4, user0, intReturner); // 04432011
        asserts(8);
        cache.insert(key3, false, user3, user0, intReturner); // 044302011
        asserts(9);
        cache.insert(key3, false, user4, user0, intReturner); // 0404302011
        asserts(10);
        cache.elements(key3, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user0, user4, user0, user4, user3, user0, user2, user0, user1, user1}));
        cache.sublist(key3, 0, 100, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user0, user4, user0, user4, user3, user0, user2, user0, user1, user1}));
        cache.sublistAndUpdateTtl(key3, 3, 3, 1, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user4, user3, user0}));
        cache.ttl(key3, intReturner);
        asserts(1);
        Thread.sleep(1200);
        cache.ttl(key3, intReturner);
        asserts(-2);
        cache.offer(key3, Arrays.asList(new AppUser[]{user0, user4, user0, user4, user3, user0, user2, user0, user1, user1}), intReturner);
        asserts(10);
        cache.removeFirst(key3, user0, intReturner);
        asserts(1);
        cache.removeLast(key3, user0, intReturner); // 40430211
        asserts(1); 
        cache.elements(key3, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user4, user0, user4, user3, user0, user2, user1, user1}));
        cache.remove(key3, user0, intReturner); // 443211
        asserts(2); 
        cache.poll(key3, valueReturner);
        asserts(user4); 
        cache.pollTail(key3, valueReturner); // 4321
        asserts(user1);
        cache.retains(key3, 1, 2, boolReturner); // 32
        asserts(true);
        cache.elements(key3, valuesReturner);
        asserts(Arrays.asList(new AppUser[]{user3, user2}));
        cache.deleteAllData(boolReturner);
        asserts(true);
        cache.elements(key3, valuesReturner);
        asserts(Collections.emptyList());
        Thread.sleep(1000);

        for (Map.Entry<String, Long> counter : Perf.allCounters().entrySet()) {
            print("%s : %d", counter.getKey(), counter.getValue());
        }
    }
}
