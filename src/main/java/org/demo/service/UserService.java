package org.demo.service;

import lombok.RequiredArgsConstructor;
import org.demo.model.CampaignConfigObject;
import org.demo.model.User;
import org.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;

import static org.demo.config.ExternalCacheMapConfig.*;
import static org.demo.config.LocalCacheMapConfig.*;
import static org.demo.constants.ServiceConstants.LOCAL_CACHE_THRESHOLD;
import static org.demo.constants.ServiceConstants.REDIS_CACHE_LIMIT;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public boolean addUserWithAttribute(int userId, int attributeId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.getAttributes().add(attributeId);
            userRepository.save(existingUser);
            return true;
        } else {
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.getAttributes().add(attributeId);
            userRepository.save(newUser);
            return false;
        }

    }

    public int getCampaignForUser(Integer userId) {
        User currentUser = new User();
        int cacheSuccess = 1;

        if (!userLocalWhitelistMap.get(userId).isEmpty()) { // If the User is found in local cache
            currentUser.setWhitelist(userLocalWhitelistMap.get(userId));
        } else if (!userWhitelistExternalMap.get(userId).isEmpty()) {
            currentUser.setWhitelist(userWhitelistExternalMap.get(userId));// If the User is found in the external "Redis" cache
            cacheSuccess = 2;
        } else {
            Optional<User> optionalUser = userRepository.findById(userId);// Else forced to Retrieve the user from the database
            currentUser = optionalUser.orElseThrow(() -> new IllegalStateException("User not found for ID: " + userId));
            cacheSuccess = 3;
        }

        assert currentUser != null;
        PriorityQueue<CampaignConfigObject> whitelist = currentUser.getWhitelist();
        HashSet<Integer> blacklist = currentUser.getBlacklist();

        if (whitelist.isEmpty()) {
            throw new IllegalStateException("Whitelist is empty for user with ID: " + userId);
        }

        CampaignConfigObject campaignConfig = whitelist.peek();
        Integer campaignId = campaignConfig.getCampaignId();

        // Check and update whitelist based on current capacity
        if (campaignConfig.getCapacity() > 1) {// If capacity larger than 1, still able to advertise to user
            campaignConfig.setCapacity(campaignConfig.getCapacity() - 1);
            whitelist.poll();  // Remove the head
            whitelist.add(campaignConfig);  // Re-add the updated campaignConfig

        } else {// If capacity is 1, just pop the head without re-adding
            whitelist.poll();
            blacklist.add(campaignId);
        }

        currentUser.setBlacklist(blacklist);
        currentUser.setWhitelist(whitelist);

        refreshCacheState(userId, cacheSuccess, whitelist);

        return campaignId;

    }

    private static void refreshCacheState(Integer userId, int cacheSuccess, PriorityQueue<CampaignConfigObject> whitelist) {
        switch (cacheSuccess) {
            case 1:   // Found in local cache - just update whitelist queue
                getLocalUserWhitelistMapMap().replace(userId, whitelist);
                return;
            case 2: { // Found in Redis only - add also to local cache
                if (localCacheCounter < LOCAL_CACHE_THRESHOLD) {
                    getLocalUserWhitelistMapMap().putIfAbsent(userId, whitelist);
                    localCacheCounter++;
                }
                return;
            }
            case 3: { // Default DB call was necessary - add to local cache and Redis
                if (localCacheCounter < LOCAL_CACHE_THRESHOLD) {
                    getLocalUserWhitelistMapMap().putIfAbsent(userId, whitelist);
                    localCacheCounter++;
                }
                if (externalCacheCounter < REDIS_CACHE_LIMIT) {
                    getExternalUserWhitelistMapMap().putIfAbsent(userId, whitelist);
                    externalCacheCounter++;
                }else RedisService.updateRedisData(userId, whitelist); // Replacing with the least used - avoid overload
            }
        }
    }


}
