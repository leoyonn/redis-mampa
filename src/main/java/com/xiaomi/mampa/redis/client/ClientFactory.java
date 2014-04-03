/**
 * ClientFactory.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Apr 3, 2014 10:25:55 AM
 */
package com.xiaomi.mampa.redis.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for {@link IClient}. <BR>
 * In Oms project redis is used mainly as local-machine-cache, {@link #getLocalClient()} satisfies most times.
 * 
 * @author leo
 */
public class ClientFactory {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 6379;
    private static final Map<String, RedisMampaClient> CLIENTS = new HashMap<String, RedisMampaClient>();

    private static IClient localClient;

    /**
     * Get a client connecting to #host:#port
     * 
     * @param host
     * @param port
     * @return
     */
    public static synchronized IClient get(String host, int port) {
        if (DEFAULT_HOST.equals(host) && DEFAULT_PORT == port) {
            if (localClient == null) {
                localClient = new RedisMampaClient(DEFAULT_HOST, DEFAULT_PORT);
            }
            return localClient;
        }
        String key = host + ":" + port;
        RedisMampaClient client = CLIENTS.get(key);
        if (client == null) {
            client = new RedisMampaClient(host, port);
            CLIENTS.put(key, client);
        }
        return client;
    }

    /**
     * Get a client connecting to {@value DEFAULT_HOST}:{@value DEFAULT_PORT}
     * 
     * @return
     */
    public static IClient getLocalClient() {
        return get(DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * Get a client connecting to {@value DEFAULT_HOST}:#port
     * 
     * @param port
     * @return
     */
    public static IClient getLocalClient(int port) {
        return get(DEFAULT_HOST, port);
    }
}
