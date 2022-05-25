package validator.Redis;

import java.util.HashMap;

import validator.Redis.MovedSlotException;
import validator.Redis.lettuce.LettuceClusterClient;
import validator.Util.Util;

/**
 * Redis Manager maintains metadata of the Redis cluster,
 * discovers which node should serve a giver key and validates 
 * the key-value data
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class RedisManager 
{
	private IRedisClusterClient cluster_coordinator;
	private HashMap<String, RedisNode> clusterNodes;	// Holds Redis nodes with theri cluster metadata
												// key is <ip>:<port>

	/**
	 * Initiates a connection with the Redis cluster
	 * @param ip	the ip of a Redis Cluster node
	 * @param port	the port of a Redis Cluster node
	 */
	public void init(String ip, String port)
	{
		System.out.print(Util.timestamp() + " Connecting to Redis node " + ip + ":" + port);
		cluster_coordinator = new LettuceClusterClient(ip, port);
		clusterNodes = new HashMap<>();
		System.out.println(" ... OK");
	}
	
	/**
	 * Refresh Redis cluster metadata (the list of Redis Cluster nodes)
	 * and the client connection for each node
	 */
	public void updateClusterMetadata()
	{
		System.out.print(Util.timestamp() + " Discover Redis Cluster metadata");
		// Forget previous partition metadata
		// Close all connections to standalone nodes and reset nodes list
		for (RedisNode rn : clusterNodes.values()) {
			rn.disconnect();
		}
		clusterNodes = null;
		
		clusterNodes = cluster_coordinator.getPartitionMappings();
		for (RedisNode rn : clusterNodes.values()) {
			rn.connect();
		}
		System.out.println(" ... OK");
	}

	/**
	 * Checks if the given key is served by the expected node
	 * and the value is not corrupted 
	 * @param key	the key to validate
	 * @param valu	the expected value
	 * @return ValidationResult (@see antonis.Redis.ValidationResult)
	 */
	public ValidationResult validateKey(String key, String value) 
	{
		String expectedNode_ipPort = keyBelongsToNode(key);
		if (expectedNode_ipPort == null) {
			return new ValidationResult(ValidationResult.Status.SLOT_UNKNOWN, 
							"There is no known host to serve key: " + key);
		}

		RedisNode rnode = clusterNodes.get(expectedNode_ipPort);		
		try {
			String reply = rnode.cmdGet_localNode(key);
			// System.out.println("Reply: " + reply);
			if (value.equals(reply) == false) {
				return new ValidationResult(ValidationResult.Status.VALUE_MISSMATCH, 
								"Missmatch value for key " + key + " Expected: " +
								value + " Got: " + reply + " from " + rnode.connectionHost());
			}
		} catch (MovedSlotException ex) {
			return new ValidationResult(ValidationResult.Status.SLOT_MOVED, 
								"Slot for key " + key + " not found on Node " + 
								rnode.connectionHost() + " " + ex.getMessage());
		}

		return new ValidationResult(ValidationResult.Status.OK, "OK");
	}

	/**
	 * Get the Redis node that is expected to serve the given key
	 * @param key
	 * @return	the node as a String: <ip>:<port> or null if no node serves the key
	 */
	private String keyBelongsToNode(String key)
	{
		int slot = keyToHashSlot(key);
		for (RedisNode p : clusterNodes.values()) {
			if (p.isSlotIncluded(slot)) {
				return (p.getIP() + ":" + p.getPort());
			}
		}
		return null;
	}

	/**
	 * Maps a key to a hash slot
	 * @param key
	 * @return the key slot is an integer
	 */
	private int keyToHashSlot(String key)
	{
		return (CRC16.crc16(key.getBytes()) % 16384);
	}

	public void shutdown()
	{
		cluster_coordinator.shutdown();
		for (RedisNode rn : clusterNodes.values()) {
			rn.disconnect();
		}
	}

	// TESTING
	public void printClusterMetadata() 
	{
		System.out.println("--- Partitions ---");
        for (RedisNode pm : clusterNodes.values()) {
            System.out.println(pm.toString());
        }
	}
}
