package com.sahlbach.maven.delivery;

import java.util.List;

/**
 * @author Andreas Sahlbach
 *         Date: 8/3/11
 *         Time: 5:18 PM
 */
public class Delivery {

    /**
     * id of this job for reference
     * @parameter
     * @required
     */
    private String id;

    /**
     * ordered list of DeliveryJobs for this Delivery
     * @parameter
     */
    private List<Job> jobs;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public List<Job> getJobs () {
        return jobs;
    }

    public void setJobs (List<Job> jobs) {
        this.jobs = jobs;
    }
}
