package org.demo.repository;

import org.demo.model.CampaignConfigObject;
import org.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.PriorityQueue;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Query("UPDATE User u SET u.whitelist = :whitelist, u.blacklist = :blacklist WHERE u.id = :userId")
    void updateUserLists(@Param("whitelist") PriorityQueue<CampaignConfigObject> whitelist,
                         @Param("blacklist") HashSet<Integer> blacklist,
                         @Param("userId") Integer userId);
}