package org.demo.config;

import org.demo.model.CampaignConfigObject;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import static org.demo.constants.ServiceConstants.CACHE_RESET;

/** REDIS MOCK **/
@Configuration
public class ExternalCacheMapConfig {
    public static int externalCacheCounter = CACHE_RESET;

    public static TreeMap<Integer, PriorityQueue<CampaignConfigObject>> userWhitelistExternalMap = new TreeMap<>();

    public static Map<Integer, PriorityQueue<CampaignConfigObject>> getExternalUserWhitelistMapMap() {
        return userWhitelistExternalMap;

    }


}


