package org.demo.config;

import org.demo.model.CampaignConfigObject;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import static org.demo.constants.ServiceConstants.CACHE_RESET;

@Configuration
public class LocalCacheMapConfig {
    public static int localCacheCounter = CACHE_RESET;
    public static Map<Integer, PriorityQueue<CampaignConfigObject>> userLocalWhitelistMap = new ConcurrentHashMap<>();

    public static Map<Integer, PriorityQueue<CampaignConfigObject>> getLocalUserWhitelistMapMap() {
        return userLocalWhitelistMap;
    }

}


