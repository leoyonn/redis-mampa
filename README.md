# Asynchronous Redis Client Using Mampa And Netty #

Usage of underlying IClient: 
<code>
    IClient client = RedisMampaClient.get("10.237.12.19", 6379);
    client.lpush("x", "y");
    ...
    // or:
    IClient client = RedisMampaClient.getLocalClient();
    // ...
</code>

Usage of ICache:
<code>
    ICache<String, AppUser> cache = new ThriftCache<AppUser>(AppUser.class);
    cache.set(key, user1);
    ...
</code>

## V0.0.2 ##
  1. add ICache, BaseCache and ThriftCache
  2. add unit test and using demo for caches
  3. add client-factory which can return local-client

## V0.0.1 ##
  1. Supporting these Commands:
  
   * ping, type, exists,
   * expire, ttl, 
   * get, getex, set, setex, setnx,
   * lindex, linsert, llen, lpop, lpush, lpush(multi), lpushx, lrange, lrangeex, lrem, lset, ltrim, 
   * rpop, rpoplpush, rpush, rpush(multi), rpushx, 
   * del, del(multi),
   * flushall, flushdb,
   * shutdown(client),

  2. Unittest for these commands;
  3. ICache on these commands;

