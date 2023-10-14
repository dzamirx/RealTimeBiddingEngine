package org.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.PriorityQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private HashSet<Integer> attributes;
    private HashSet<Integer> blacklist;
    private PriorityQueue<CampaignConfigObject> whitelist;


}
