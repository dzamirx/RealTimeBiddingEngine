package org.demo.util;

import org.demo.model.CampaignConfigObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class UserPriorityQueueUtil {
    private final Map<Integer, PriorityQueue<CampaignConfigObject>> userPriorityQueues;
    public UserPriorityQueueUtil() {

        this.userPriorityQueues = new HashMap<>();
    }

    public void addUser(Integer userId) {

        userPriorityQueues.put(userId, new PriorityQueue<>());
    }

    public void addCompanyToUserQueue(Integer userId, Integer companyId, int capacity, int priority) {
        CampaignConfigObject campaignConfigObject = new CampaignConfigObject(companyId, capacity, priority);
        userPriorityQueues.computeIfAbsent(userId, k -> new PriorityQueue<>()).add(campaignConfigObject);
    }

    public PriorityQueue<CampaignConfigObject> getUserQueue(Integer userId) {

        return userPriorityQueues.get(userId);
    }


}


