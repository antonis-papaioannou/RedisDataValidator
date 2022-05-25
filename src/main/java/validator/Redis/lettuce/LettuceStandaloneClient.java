package validator.Redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import validator.Redis.IRedisClient;
import validator.Redis.MovedSlotException;

/**
 * A Redis standalone client implementation based on the lettuce java client API
 * (@link https://lettuce.io)
 */
public class LettuceStandaloneClient implements IRedisClient 
{
    private String targetIP = null;
    private String port     = null;
    private RedisClient redisClient = null;
    private StatefulRedisConnection<String, String> connection = null;
    private RedisCommands<String, String> sync = null;

    public LettuceStandaloneClient(String targetIP, String port) {
        this.targetIP = targetIP;
        this.port   = port;
        // Syntax: redis://[password@]host[:port]
        redisClient = RedisClient.create("redis://" + targetIP + ":" + port);
        connection = redisClient.connect();

        sync = connection.sync();
    }

    @Override
    public void simpleSet(String key, String value) {
        sync.set(key, value);
    }

    @Override
    public String simpleGet(String key) {
        try {
            return sync.get(key);
        } catch (RedisCommandExecutionException ex) {
            if (ex.getMessage().substring(0,5).equals("MOVED")) {
				// System.out.println("^^^^^ MOVED " + ex_msg);
                throw new MovedSlotException(ex.getMessage(), ex);
			}
        }
        return null;
    }

    @Override
    public void shutdown() {
        if (connection != null) { 
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}
