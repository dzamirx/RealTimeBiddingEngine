package org.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class CampaignConfigObject implements Comparable<CampaignConfigObject> {
    private Integer campaignId;
    private int capacity;
    private int priority;


    @Override// Higher priority comes first
    public int compareTo(CampaignConfigObject other) {
        return Integer.compare(other.priority, this.priority);
    }


}
