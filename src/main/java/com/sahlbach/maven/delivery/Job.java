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
     * @parameter
     */
    private Upload upload;

    /**
     * @parameter 
     */
    private Exec exec;

    public void execute(DeliveryMojo mojo) throws MojoExecutionException, MojoFailureException {
        if(upload != null && exec != null)
            throw new MojoExecutionException("Job must either be exec or upload.");
        if(upload != null)
            upload.execute(mojo);
        else
            exec.execute(mojo);
    }
}
