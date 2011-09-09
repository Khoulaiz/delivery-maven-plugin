/*
 * Copyright 2011$ Andreas Sahlbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sahlbach.maven.delivery;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

public class AbstractSshRemoteJob extends AbstractRemoteJob {

    /**
     * if set, use this executable as external program for the upload
     * @parameter
     */
    private String executable;

    /**
     * keyfile to use for authentication
     * @parameter
     */
    private File keyfile;

    /**
     * @parameter
     * password to use to unlock keyfile
     */
    private String keyPassword;
    /**
     * Type of upload
     * @parameter
     * @required
     */
    private String type;

    public AbstractSshRemoteJob mergeWith(AbstractSshRemoteJob remoteJob) throws MojoExecutionException {
        super.mergeWith(remoteJob);
        if(type != null && (remoteJob.getType() != null) && !type.equals(remoteJob.getType()))
            throw new MojoExecutionException("Remote Jobs must be from same type");
        if(remoteJob.getType() != null)
            type = remoteJob.getType();
        if(remoteJob.getExecutable() != null)
            setExecutable(remoteJob.getExecutable());
        if(remoteJob.getKeyfile() != null)
            setKeyfile(remoteJob.getKeyfile());
        if(remoteJob.getKeyPassword() != null)
            setKeyPassword(remoteJob.getKeyPassword());
        return this;
    }

    public String getExecutable () {
        return executable;
    }

    public void setExecutable (String executable) {
        this.executable = executable;
    }

    public File getKeyfile () {
        return keyfile;
    }

    public void setKeyfile (File keyfile) {
        this.keyfile = keyfile;
    }

    public String getKeyPassword () {
        return keyPassword;
    }

    public void setKeyPassword (String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getType () {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
