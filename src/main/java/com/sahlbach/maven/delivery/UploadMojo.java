package com.sahlbach.maven.delivery;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.util.Collections;
import java.util.List;

/**
 * @author Andreas Sahlbach
 *         Date: 8/3/11
 *         Time: 5:18 PM
 */
/**
 * Goal that uploads a couple of filesets to a specific server using
 * a specific transportation type. It doesn't bind to a life-cycle phase
 * per default
 *
 * @goal upload
 * @requiresDirectInvocation true
 *
 */
public class UploadMojo extends AbstractMojo {
    /**
     * List of deliveries;
     * @parameter
     * @required
     */
    private List<Delivery> deliveries = Collections.emptyList();

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

        for (Delivery delivery : deliveries) {

            for (DeliveryArtifact deliveryArtifact : delivery.getArtifacts()) {
                Artifact artifact;
                try {
                    artifact = new DefaultArtifact( deliveryArtifact.toString() );
                }
                catch ( IllegalArgumentException e ) {
                    throw new MojoFailureException( e.getMessage(), e );
                }
                ArtifactRequest request = new ArtifactRequest();
                request.setArtifact( artifact );
                request.setRepositories( remoteRepos );

                getLog().debug( "Resolving artifact " + artifact + " from " + remoteRepos );

                ArtifactResult result;
                try {
                    result = repoSystem.resolveArtifact( repoSession, request );
                }
                catch ( ArtifactResolutionException e ) {
                    throw new MojoExecutionException( e.getMessage(), e );
                }
                getLog().info( "Resolved artifact " + artifact + " to " + result.getArtifact().getFile() + " from "
                               + result.getRepository() );
                
            }
        }
    }
}
