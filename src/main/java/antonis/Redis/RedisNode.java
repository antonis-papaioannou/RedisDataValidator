package antonis.Redis;

import antonis.Redis.lettuce.LettuceStandaloneClient;
import antonis.Util.Util;

/**
 * Holds metada info regarding a Redis node and the partitions it hosts
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class RedisNode 
{
    private String  ip      = null;
    private String  port    = null;
    private long    slot_range_start = -1; //inclusive boundary
    private long    slot_range_end   = -1;  //inclusive boundary

    /* This is a client to a Standalone Redis node even if the 
    * the node is part of a Redis Cluster (the client does not
    * follow redirects (@see antonis.Redis.IRedisClient) */
    private IRedisClient client = null;

    public RedisNode(String ip, String port)
    {
        this.ip     = ip;
        this.port   = port;
    }
    
    public RedisNode(String ip, String port, long start_slot_range, long end_slot_range) 
    {
        this.ip     = ip;
        this.port   = port;
        this.slot_range_start   = start_slot_range;
        this.slot_range_end     = end_slot_range;
    }

    public String getIP() 
    { 
        return this.ip; 
    }
    
    public String getPort() 
    { 
        return this.port; 
    }

    public long getSlotRangeStart() 
    { 
        return slot_range_start; 
    }

    public long getSlotRangeEnd() 
    { 
        return slot_range_end; 
    }
    
    /**
     * Creats a persistent Redis Client connection to the targeted Redis node.
     * The targeted node may be part of a Redis Cluster
     * but the clients acts as if the node was a standalone node (@see antonis.Redis.IRedisClient).
     */
    public void connect()
    {
        // System.out.println(AntonisUtil.pointInCode() + " Connecting to " + ip + ":" + port);
        client = new LettuceStandaloneClient(ip, port);
    }

    /**
     * Executes the command on the targeted node only (does not follow redirects)
     * @param key
     * @return The string of the value
     */
    public String cmdGet_localNode(String key)
    {
        return client.simpleGet(key);
    }

    public IRedisClient getClient() {
        if (client == null) {
            connect();
        }
        return this.client;
    }

    /**
     * Checks whether the slot is served by this Redis node
     * @param slot
     * @return true if the node serves the given slot, otherwise false
     */
    public boolean isSlotIncluded(int slot)
    {
        if ( (slot >= slot_range_start)  &&
             (slot <= slot_range_end) ) {
                 return true;
        }
        return false;
    }

    /**
     * Closes the persistent Redis client connection
     */
    public void disconnect() 
    {
        client.shutdown();
    }

    /**
     * @return the <ip>:<port> of the Redis connection as a string
     */
    public String connectionHost()
    {
        return (ip + ":" + port);
    }
    
    public String toString() 
    {
        return ip + ":" + port + " " + slot_range_start + " " + slot_range_end;
    }
}
