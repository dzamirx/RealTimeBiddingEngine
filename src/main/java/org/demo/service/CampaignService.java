package org.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.config.ExternalCacheMapConfig;
import org.demo.model.Campaign;
import org.demo.model.CampaignConfigObject;
import org.demo.model.User;
import org.demo.repository.CampaignRepository;
import org.demo.repository.UserRepository;
import org.demo.util.UserPriorityQueueUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.PriorityQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;

    private final UserRepository userRepository;

    private final UserPriorityQueueUtil userPriorityQueueUtil;


    /** Heavy operation - matching campaigns to users **/
    public void updateUsersWhitelistWithMatchingCampaigns() {
        List<Campaign> campaigns = campaignRepository.findAll();
        List<User> users = userRepository.findAll();

        if (campaigns.isEmpty() || users.isEmpty()) {
            log.warn("Campaigns or Users data is null or empty.");
            return;
        }

        users.parallelStream().forEach(user -> {
            Integer userId = user.getUserId();

            campaigns.parallelStream()
                    .filter(campaign ->
                            !user.getBlacklist().contains(campaign.getCampaignId()) &&
                                    user.getAttributes().containsAll(campaign.getAttributes())
                    )
                    .forEach(campaign -> {
                        userPriorityQueueUtil.addCompanyToUserQueue(
                                userId,
                                campaign.getCampaignId(),
                                campaign.getCapacity(),
                                campaign.getPriority()
                        );
                    });

            // Store the user and matching whitelist in the External Redis Cache and UsersDB - should be done as bulk
            PriorityQueue<CampaignConfigObject> userWhitelist = userPriorityQueueUtil.getUserQueue(userId);
            if (userWhitelist != null) {
                ExternalCacheMapConfig.getExternalUserWhitelistMapMap().put(userId, userWhitelist);
                userRepository.updateUserLists(userWhitelist,user.getBlacklist(),userId);
            }

        });
    }
}