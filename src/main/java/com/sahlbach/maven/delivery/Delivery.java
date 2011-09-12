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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

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
     * @param toMerge delivers the overwritten values of the delivery
     * @throws org.apache.maven.plugin.MojoExecutionException in case of merge conflicts
     * @return the merged instance (this) for call chaining
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

    /**
     * orders the (merged) jobs according to their before and after attributes.
     * @throws MojoExecutionException in case the order attributes contain errors (non existing job references)
     */
    public void orderJobs() throws MojoExecutionException {
        List<Job> result = Lists.newArrayList(Iterables.filter(jobs, new Predicate<Job>() {
            @Override
            public boolean apply(Job input) {
                return (StringUtils.isEmpty(input.getAfter()) && StringUtils.isEmpty(input.getBefore()));
            }
        }));
        List<Job> toSort = Lists.newArrayList(Iterables.filter(jobs, new Predicate<Job>() {
            @Override
            public boolean apply(Job input) {
                return (!StringUtils.isEmpty(input.getAfter()) || !StringUtils.isEmpty(input.getBefore()));
            }
        }));
        boolean sortedAtLeaseOne;
        do {
            sortedAtLeaseOne = false;
            for (Job job : toSort) {
                if(!StringUtils.isEmpty(job.getAfter())) {
                    int foundIndex = Iterables.indexOf(result, new FindJobViaJobId(job.getAfter()));
                    if(foundIndex > -1) {
                        result.add(foundIndex+1,job);
                        sortedAtLeaseOne = true;
                    }
                } else {
                    int foundIndex = Iterables.indexOf(result, new FindJobViaJobId(job.getBefore()));
                    if(foundIndex > -1) {
                        result.add(foundIndex,job);
                        sortedAtLeaseOne = true;
                    }
                }
                toSort.remove(job);
            }
        } while(sortedAtLeaseOne);
        if(!toSort.isEmpty()) {
            throw new MojoExecutionException("Could not resolve the before or after references of job: "
                                             + Joiner.on(",").join(Lists.transform(toSort,new Function<Job, String>() {
                @Override
                public String apply(Job input) {
                    return input.getId();
                }
            })));
        }
        jobs = result;
    }

    private class FindJobViaJobId implements Predicate<Job> {

        private String toFind;

        public FindJobViaJobId(String toFind) {
            this.toFind = toFind;
        }

        @Override
        public boolean apply(Job input) {
            return toFind.equals(input.getId());
        }
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
