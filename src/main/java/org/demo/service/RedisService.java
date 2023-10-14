package org.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.demo.model.CampaignConfigObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import static org.demo.config.ExternalCacheMapConfig.userWhitelistExternalMap;
import static org.demo.config.LocalCacheMapConfig.getLocalUserWhitelistMapMap;

@Service
@Slf4j
public class RedisService {

    private static Jedis jedis;

    public RedisService(String redisHost, int redisPort) {
        jedis = new Jedis(redisHost, redisPort);
    }

    public static void updateRedisData(Integer userId, PriorityQueue<CampaignConfigObject> whitelist) {
        RedisService redisService = new RedisService("localhost", 6379);
        String redisKey = "user:" + userId;
        String serializedWhitelist = serializeWhitelist(whitelist); // Serialize the whitelist data (assuming CampaignConfigObject is serializable)

        removeLeastUsedEntryFromRedis(0,0);
        jedis.set(redisKey, serializedWhitelist); // Set the Redis key with the serialized whitelist

        log.info("Updated Redis data for user ID: " + userId);
        log.info("Whitelist updated in Redis for user ID: " + userId);
        redisService.closeConnection();

    }
    private static String serializeWhitelist(PriorityQueue<CampaignConfigObject> whitelist) {
        // Serialize the whitelist to a format suitable for storage in Redis
        StringBuilder serializedWhitelist = new StringBuilder();
        for (CampaignConfigObject configObject : whitelist) {
            serializedWhitelist.append(configObject.toString()).append(",");
        }
        return serializedWhitelist.toString();
    }

    public static void removeLeastUsedEntryFromRedis(int start, int stop) {
        String redisSortedSet = "redis_sorted_set";
        jedis.zremrangeByRank(redisSortedSet, start, stop); // Removes the least used entry from the sorted set

    }

    public static void retrieveTopUsersFromRedis(int range) {
        TreeMap<Integer, PriorityQueue<CampaignConfigObject>> sortedMap = new TreeMap<>(userWhitelistExternalMap);

        int count = 0;
        for (Map.Entry<Integer, PriorityQueue<CampaignConfigObject>> entry : sortedMap.descendingMap().entrySet()) {
            if (count >= range) {
                break;
            }

            Integer userId = entry.getKey();
            PriorityQueue<CampaignConfigObject> whitelist = entry.getValue();

            getLocalUserWhitelistMapMap().putIfAbsent(userId, whitelist);
            count++;
        }
    }
    public void closeConnection() {
        jedis.close();
    }


}
