/*
 * Copyright 2011 Andreas Sahlbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sahlbach.maven.delivery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.maven.plugin.MojoExecutionException;

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
     * description of this delivery
     * @parameter
     */
    private String description;

    /**
     * ordered list of DeliveryJobs for this Delivery
     * @parameter
     */
    private List<Job> jobs = Collections.emptyList();

    /**
     * creates a merged version of two deliveries.
     * jobs are deeply merged
     * @param defaultDelivery delivers the base values of the delivery
     * @param toMerge delivers the overwritten values of the delivery
     * @throws org.apache.maven.plugin.MojoExecutionException in case of merge conflicts
     */
    public Delivery mergeWith(Delivery toMerge) throws MojoExecutionException {
        setId(toMerge.getId());
        if(toMerge.getDescription() != null)
            setDescription(toMerge.getDescription());
        Map<String,Job> mappedJobs = Maps.newHashMapWithExpectedSize(getJobs().size() + toMerge.getJobs().size());
        List<Job> resultJobs = Lists.newArrayListWithCapacity(getJobs().size() + toMerge.getJobs().size());
        for (Job job : getJobs()) {
            Job newJob = new Job();
            newJob.mergeWith(job);
            if(newJob.getId() != null)
                mappedJobs.put(newJob.getId(),newJob);
            resultJobs.add(newJob);
        }
        for (Job job : toMerge.getJobs()) {
            if((job.getId() != null) && mappedJobs.get(job.getId()) != null) {
                Job existingJob = mappedJobs.get(job.getId());
                existingJob.mergeWith(job);
            } else {
                resultJobs.add(new Job().mergeWith(job));
            }
        }
        setJobs(resultJobs);
        return this;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
