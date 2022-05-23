package antonis.Redis;

/**
 * Redis Standalone node client operations
 * It performs the operation on the connected node.
 * It does not follow REDIRECT or ASK replies from server
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public interface IRedisClient 
{
    /**
     * Performs a simple put operation on Strings
     * It performs the operation on the connected node.
     * It does not follow REDIRECT or ASK replies from server
     * @param key   the hey
     * @param value the value
     */
    public void simpleSet(String key, String value);

    /**
     * Simple get operation for the given key
     * It performs the operation on the connected node.
     * It does not follow REDIRECT or ASK replies from server
     * @param key the key
     * @return  the value of the key
     */
    public String simpleGet(String key);

    /**
     * Closes the persistent connection with the Redis node
     */
    public void shutdown();

}