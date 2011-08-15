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
     * description of this delivery
     * @parameter 
     */
    private String description;

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
