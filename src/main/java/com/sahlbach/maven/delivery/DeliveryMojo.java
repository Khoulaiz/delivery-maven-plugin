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

import java.util.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

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
     /**
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
     * if not set, and interactive is true, ask interactively for the jobs
     * if not set, and interactive is false, fail
     * @parameter expression="${delivery.ids}"
     */
    private String deliveryIds;

    /**
     * true: ask for more information if needed
     * false: fail if information is missing
     * @parameter expression="${settings.interactiveMode}"
     */
    private boolean interactiveMode = true;

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     *
     * @component
     */
    private RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     *
     * @parameter expression="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;

    /**
     * The project's remote repositories to use for the resolution.
     *
     * @parameter expression="${project.remoteProjectRepositories}"
     * @readonly
     */
    private List<RemoteRepository> remoteRepos;

    /**
     * Allows user prompting
     * @component
     * @readonly
     */
    private Prompter prompter;

    /**
     * @parameter expression="${project.version}"
     */
    private String projectVersion;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if(skip) {
            getLog().info("Delivery skipped.");
            return;
        }

        Set<String> deliveriesToExecute = new HashSet<String>();

        if(deliveryIds != null) {
            int enteredDeliveries = cleanSelection(deliveriesToExecute, deliveryIds);
            if(enteredDeliveries != deliveriesToExecute.size())
                throw new MojoFailureException("Delivery interrupted, ambiguous list of IDs in 'delivery.ids' given");

        } else if(deliveries.size() == 1) {
            deliveriesToExecute.add(deliveries.get(0).getId());

        } else if(interactiveMode) {
            try {
                StringBuilder prompt = new StringBuilder("Enter comma separated list of deliveryIDs to execute. Available deliveries are:\n");
                for (Delivery delivery : deliveries) {
                    prompt.append("* ").append(delivery.getId());
                    if(delivery.getDescription() != null)
                        prompt.append(" (").append(delivery.getDescription()).append(")").append("\n");
                }

                while(deliveriesToExecute.isEmpty()) {

                    String prompted = prompter.prompt(prompt.toString());

                    if(prompted.length() == 0)
                        throw new MojoFailureException("Delivery interrupted, empty prompt.");

                    int selected = cleanSelection(deliveriesToExecute, prompted);
                    if(deliveriesToExecute.size() != selected) {
                        deliveriesToExecute.clear();
                    }
                }
            } catch (PrompterException e) {
                throw new MojoExecutionException("Prompt exception:",e);
            }
        } else {
            throw new MojoFailureException("You need to define a comma separated list of delivery IDs in 'delivery.ids' to execute");
        }

        for (Delivery delivery : deliveries) {

            if(deliveriesToExecute.contains(delivery.getId())) {

                for (Job job : delivery.getJobs()) {
                    job.execute(this);
                }
            }
        }
    }

    private int cleanSelection(Set<String> deliveriesToExecute, String prompted) {
        deliveriesToExecute.clear();
        String[] selected = prompted.split(",");
        for (String select : selected) {
            for (Delivery delivery : deliveries) {
                if(select.trim().equalsIgnoreCase(delivery.getId().trim())) {
                    deliveriesToExecute.add(delivery.getId());
                    break;
                }
            }
        }
        return selected.length;
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

    public boolean isInteractiveMode() {
        return interactiveMode;
    }

    public void setInteractiveMode(boolean interactive) {
        this.interactiveMode = interactive;
    }

    public Prompter getPrompter() {
        return prompter;
    }

    public String getProjectVersion() {
        return projectVersion;
    }
}
