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

    public RepositorySystemSession getRepoSession () {
        return repoSession;
    }

    public List<RemoteRepository> getRemoteRepos () {
        return remoteRepos;
    }
}
