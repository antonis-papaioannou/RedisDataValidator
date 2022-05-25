package validator.Redis.lettuce;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import validator.Redis.IRedisClusterClient;
import validator.Redis.RedisNode;
import validator.Util.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;;

/**
 * A Redis Cluster client implementation based on the lettuce java client API
 * (@link https://lettuce.io)
 */
public class LettuceClusterClient implements IRedisClusterClient 
{
    RedisClusterClient redCluster = null;
    StatefulRedisClusterConnection<String, String> connection = null;
    RedisAdvancedClusterCommands<String, String> sync = null;

    public LettuceClusterClient(String host, String port) {
        // Syntax: redis://[password@]host[:port]
        redCluster = RedisClusterClient.create("redis://" + host + ":" + port);
        connection = redCluster.connect();
        sync = connection.sync();   // synchronus api
    }

    @Override
    public void simpleSet(String key, String value) {
        sync.set(key, value);
    }

    @Override
    public String simpleGet(String key) {
        return sync.get(key);
    }

    @Override
    public void shutdown() {
        if (connection != null) {
            connection.close();
        }
        if (redCluster != null) {
            redCluster.shutdown();
        }
    }

    /**
    * Get the Redis Cluster partition mapping metadata
    * (@see antonis.Redis.IRedisClusterClient)
    */
    @Override
    public HashMap<String, RedisNode> getPartitionMappings() 
    {
        HashMap<String, RedisNode> ret = new HashMap();
        if (sync == null) {
            System.out.println(Util.pointInCode() + " No active Redis connectio");
            return ret;
        }
        
        //Lettuce returns a List of Objects. Each object of the list contains an arrayList representing a partition
        //format: <slot_start(long)>, <slot_end(long), <host info (ArrayList)>
        //The format of the nested ArrayList: <host(string)>, <port(long)>, <nodeid (string)> 
        List<Object> partitions = sync.clusterSlots();
        for (Object p : partitions) {
            // System.out.println(p);
            ArrayList partition = (ArrayList) p;
            // System.out.println("arr: " + partition + " elements:  " + partition.size());
            // for (int i = 0; i < partition.size(); i++) {
            //     System.out.println("element " + i + ": " + partition.get(i) + " type: " + partition.get(i).getClass().getName());
            // }
                
            long slot_start = (long) partition.get(0);
            long slot_end = (long) partition.get(1);

            ArrayList nestedInfo = (ArrayList) partition.get(2);
            String ip = (String) nestedInfo.get(0);
            String port = (String) String.valueOf(nestedInfo.get(1));
            // String hash_node_id = (String) nestedInfo.get(2);
            // for (int j = 0; j < nestedInfo.size(); j++) {
            //     System.out.println("\telement " + j + ": " + nestedInfo.get(j) + " type: " + nestedInfo.get(j).getClass().getName());
            // }

            ret.put(ip+":"+port, new RedisNode(ip, port, slot_start, slot_end));
        }

        return ret;
    }
    
}
