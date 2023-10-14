package org.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.service.CampaignService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static org.demo.config.ExternalCacheMapConfig.externalCacheCounter;
import static org.demo.constants.ServiceConstants.*;
import static org.demo.service.RedisService.removeLeastUsedEntryFromRedis;
import static org.demo.service.RedisService.retrieveTopUsersFromRedis;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final CampaignService campaignService;

    /** Major update - Match users to campaigns and place Redis (every 6 Hours + startup) **/
    @Scheduled(cron = "0 0 */6 * * *")
    @EventListener(ApplicationReadyEvent.class)
    public void updateUsersWhitelistJob() {
        campaignService.updateUsersWhitelistWithMatchingCampaigns();

    }

    /** Medium update - Update Redis with Users DB data (every 3 Hours) **/
    @Scheduled(cron = "0 0 */3 * * *")
    public void checkAndUpdateRedisData() {
        if (externalCacheCounter > REDIS_CACHE_LIMIT) {
            removeLeastUsedEntryFromRedis(0, REDIS_CACHE_SCHEDULED_EVICT_RANGE);
            log.info("Updated Redis " + REDIS_CACHE_SCHEDULED_EVICT_RANGE + " most unused objects");
        }

    }

    /** Minor update - update local cache with Redis top 5000 hit users (Hourly) **/
    @Scheduled(cron = "0 0 0/1 * * *")
    public void checkAndUpdateLocalData() {
        retrieveTopUsersFromRedis(LOCAL_CACHE_THRESHOLD);
    }


}
