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
