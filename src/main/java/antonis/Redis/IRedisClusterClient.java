package antonis.Redis;

import java.util.HashMap;
import antonis.Redis.RedisNode;

/**
 * Redis Cluster node client operations.
 * It extends the Standalone Redis client interface
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public interface IRedisClusterClient extends IRedisClient 
{
	/**
	 * It gets the metadata regarding the slot allocation across
	 * Redis Cluster nodes 
	 * @return A HashMap containing the mapping of Redis node IP 
	 * 		  and the corresponding RedisNode object (@see antonis.Redis.RedisNode)
	 */
	public HashMap<String, RedisNode>  getPartitionMappings();
}
