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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
     * List of default deliveries of this mojo (they can be defined fully or partially in the parent pom)
     * @parameter
     */
    private List<Delivery> deliveryManagement = Collections.emptyList();

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if(skip) {
            getLog().info("Delivery skipped.");
            return;
        }

        checkDeliveryDefinitionConsistency();

        List<Delivery> mergedDeliveries = mergeDeliveries();

        Set<String> deliveriesToExecute = new HashSet<String>();

        if(deliveryIds != null) {
            int enteredDeliveries = cleanSelection(deliveriesToExecute, deliveryIds, mergedDeliveries);
            if(enteredDeliveries != deliveriesToExecute.size())
                throw new MojoFailureException("Delivery interrupted, ambiguous list of IDs in 'delivery.ids' given");

        } else if(mergedDeliveries.size() == 1) {
            deliveriesToExecute.add(mergedDeliveries.get(0).getId());

        } else if(interactiveMode) {
            try {
                StringBuilder prompt = new StringBuilder("Enter comma separated list of deliveryIDs to execute. Available deliveries are:\n");
                for (Delivery delivery : mergedDeliveries) {
                    prompt.append("* ").append(delivery.getId());
                    if(delivery.getDescription() != null)
                        prompt.append(" (").append(delivery.getDescription()).append(")").append("\n");
                }

                while(deliveriesToExecute.isEmpty()) {

                    String prompted = prompter.prompt(prompt.toString());

                    if(prompted.length() == 0)
                        throw new MojoFailureException("Delivery interrupted, empty prompt.");

                    int selected = cleanSelection(deliveriesToExecute, prompted, mergedDeliveries);
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

        for (Delivery delivery : mergedDeliveries) {

            if(deliveriesToExecute.contains(delivery.getId())) {

                for (Job job : delivery.getJobs()) {
                    job.execute(this);
                }
            }
        }
    }

    /**
     * merges deliveryManagement deliveries with local deliveries
     * the resulting merge should contain a merge of all delivery definition. local definitions with same ids should overwrite the data of deliveryManagement
     * @return merged list of deliveries
     * @throws org.apache.maven.plugin.MojoExecutionException in case of merge conflicts
     */
    private List<Delivery> mergeDeliveries() throws MojoExecutionException {
        Map<String,Delivery> mergedMap = Maps.newHashMapWithExpectedSize(deliveryManagement.size()+deliveries.size());

        for (Delivery defaultDelivery : deliveryManagement) {
            Delivery newDelivery = new Delivery().mergeWith(defaultDelivery);
            mergedMap.put(newDelivery.getId(), newDelivery);
        }

        for (Delivery localDelivery : deliveries) {
            Delivery defaultDelivery = mergedMap.get(localDelivery.getId());
            if(defaultDelivery == null)
                defaultDelivery = new Delivery();
            defaultDelivery.mergeWith(localDelivery);
            mergedMap.put(defaultDelivery.getId(),defaultDelivery);
        }
        return Lists.newArrayList(mergedMap.values());
    }

    /**
     * checks for consistency in delivery and job definition. each delivery id has to be unique per collection. each job id has to be unique within a delivery
     * @throws MojoExecutionException in case of inconsistencies
     */
    private void checkDeliveryDefinitionConsistency() throws MojoExecutionException {

        List<String> seenIds = new ArrayList<String>(deliveryManagement.size());

        for (Delivery delivery : deliveryManagement) {
            if(seenIds.contains(delivery.getId())) {
                throw new MojoExecutionException("Duplicate delivery id "+delivery.getId()+" in deliveryManagement definition");
            } else {
                seenIds.add(delivery.getId());
                checkJobDefinitionConsistency(delivery);
            }
        }
        seenIds = new ArrayList<String>(deliveries.size());

        for (Delivery delivery : deliveries) {
            if(seenIds.contains(delivery.getId())) {
                throw new MojoExecutionException("Duplicate delivery id "+delivery.getId()+" in deliveries definition");
            } else {
                seenIds.add(delivery.getId());
                checkJobDefinitionConsistency(delivery);
            }
        }
    }

    private void checkJobDefinitionConsistency(Delivery delivery) throws MojoExecutionException {
        List<String> seenIds = new ArrayList<String>(delivery.getJobs().size());
        for (Job job : delivery.getJobs()) {
            if(job.getId() != null && seenIds.contains(job.getId())) {
                throw new MojoExecutionException("Duplicate job id "+job.getId()+" in delivery "+(delivery.getId() == null ? "<no id defined>" : delivery.getId()));
            } else {
                if(job.getId() != null)
                    seenIds.add(job.getId());
            }
        }
    }

    private static int cleanSelection(Set<String> deliveriesToExecute, String prompted, List<Delivery> myDeliveries) {
        deliveriesToExecute.clear();
        String[] selected = prompted.split(",");
        for (String select : selected) {
            for (Delivery delivery : myDeliveries) {
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

    public List<Delivery> getDeliveryManagement() {
        return deliveryManagement;
    }

    public void setDeliveryManagement(List<Delivery> deliveryManagement) {
        this.deliveryManagement = deliveryManagement;
    }
}
