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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Andreas Sahlbach
 *         Date: 8/3/11
 *         Time: 5:18 PM
 */
/**
 * Goal that uploads files or artifacts to a server and/or executes remote operations
 *
 * @goal delivery
 *
 */
public class DeliveryMojo extends AbstractMojo {
     /*
      * Skip doing the delivery
	  *
	  * @parameter expression="${delivery.skip}" default-value="false"
	  */
     private boolean skip = false;

    /**
     * List of deliveries of this mojo
     * @parameter
     * @required
     */
    private List<Delivery> deliveries = Collections.emptyList();

    /**
     * comma separated list of jobs to execute
     * if not set, execute all of them
     * @parameter default-value="${deliveryJobs}"
     */
    private String deliveryIds;

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     *
     * @component
     */
    private RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     *
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;

    /**
     * The project's remote repositories to use for the resolution.
     *
     * @parameter default-value="${project.remoteProjectRepositories}"
     * @readonly
     */
    private List<RemoteRepository> remoteRepos;


    public void execute() throws MojoExecutionException, MojoFailureException {
        
        if(skip)
            return;

        List<String> deliveriesToExecute = null;

        if(deliveryIds != null)
            deliveriesToExecute = Arrays.asList(deliveryIds.split(","));

        for (Delivery delivery : deliveries) {

            if(deliveriesToExecute != null && !deliveriesToExecute.contains(delivery.getId()))
                continue;

            for (Job job : delivery.getJobs()) {
                job.execute(this);
            }
        }
    }

    public RepositorySystem getRepoSystem () {
        return repoSystem;
    }

    public void setRepoSystem (RepositorySystem repoSystem) {
        this.repoSystem = repoSystem;
    }

    public RepositorySystemSession getRepoSession () {
        return repoSession;
    }

    public void setRepoSession (RepositorySystemSession repoSession) {
        this.repoSession = repoSession;
    }

    public List<RemoteRepository> getRemoteRepos () {
        return remoteRepos;
    }

    public void setRemoteRepos (List<RemoteRepository> remoteRepos) {
        this.remoteRepos = remoteRepos;
    }

    public boolean isSkip () {
        return skip;
    }

    public void setSkip (boolean skip) {
        this.skip = skip;
    }

    public List<Delivery> getDeliveries () {
        return deliveries;
    }

    public void setDeliveries (List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public String getDeliveryIds () {
        return deliveryIds;
    }

    public void setDeliveryIds (String deliveryIds) {
        this.deliveryIds = deliveryIds;
    }
}
