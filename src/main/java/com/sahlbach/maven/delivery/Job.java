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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * User: Andreas Sahlbach
 * Date: 06.08.11
 * Time: 09:56
 */
public class Job {

    /**
     * Allows referencing and overwriting job data defined in dependencyManagement
     * data of local jobs having the same job id as jobs defined in dependencyManagement is overwritten by the local data
     * id has to be unique within one delivery definition
     * @parameter
     */
    private String id = null;

    /**
     * Execution order of the job. Jobs are executed in the order of this number, lower numbers first.
     * In case of this order number is not set, jobs are executed in the order they are define,
     * default jobs are executed first, then local jobs.
     * @parameter
     */
    private Integer order = null;

    /**
     * @parameter
     */
    private Upload upload;

    /**
     * @parameter
     */
    private Exec exec;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public Exec getExec() {
        return exec;
    }

    public void setExec(Exec exec) {
        this.exec = exec;
    }

    public void execute(DeliveryMojo mojo) throws MojoExecutionException, MojoFailureException {
        if(upload != null && exec != null)
            throw new MojoExecutionException("Job must either be exec or upload.");
        if(upload != null)
            upload.execute(mojo);
        else
            exec.execute(mojo);
    }

    /**
     * let the given job overwrite all values, use own values as fallback
     * @param toMerge overwriting job definition
     * @throws org.apache.maven.plugin.MojoExecutionException in case of incompatible jobs to merge
     */
    public Job mergeWith(Job toMerge) throws MojoExecutionException {
        if(toMerge.getId() != null)
            setId(toMerge.getId());
        if(toMerge.getOrder() != null)
            setOrder(toMerge.getOrder());
        if((upload != null && toMerge.getExec() != null) || (exec != null && toMerge.getUpload() != null))
            throw new MojoExecutionException("Jobs must be from same type (either upload or exec)");
        if(toMerge.getUpload() != null) {
            if(upload == null)
                upload = new Upload();
            upload.mergeWith(toMerge.getUpload());
        }
        if(toMerge.getExec() != null) {
            if(exec == null)
                exec = new Exec();
            exec.mergeWith(toMerge.getExec());
        }
        return this;
    }
}
