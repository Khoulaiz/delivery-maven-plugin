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

import com.sahlbach.maven.delivery.exec.Executor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.List;

/**
 * User: Andreas Sahlbach
 * Date: 06.08.11
 * Time: 11:24
 */
public class Exec {

    /**
     * Type of upload
     * @parameter default-value="ssh"
     * @required
     */
    private String type;

    /**
     * List of commands to execute
     * @parameter
     * @required
     */
    private List<String> commands;

    /**
     * if set, use this executable as external program for the upload
     * @parameter
     */
    private String executable;

    /**
     * target directory on the remote server
     * @parameter
     * @required
     */
    private String targetDir;

    /**
     * hostname of the target server
     * @parameter
     */
    private String server;

    /**
     * port of the target server
     * @parameter
     */
    private int port;

    /**
     * remote username to use to login
     * @parameter
     */
    private String username;

    /**
     * password to use for username / password authentication
     * @parameter
     */
    private String userPassword;

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


    public void execute (DeliveryMojo mojo) throws MojoExecutionException, MojoFailureException {
        Executor executor = Executor.createExecutor(type.toLowerCase(), mojo.getLog());
        if(executor == null) {
            throw new MojoExecutionException("Can't find Executor of type " + type);
        }
        executor.execute(commands, targetDir, this);
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public List<String> getCommands () {
        return commands;
    }

    public void setCommands (List<String> commands) {
        this.commands = commands;
    }

    public String getExecutable () {
        return executable;
    }

    public void setExecutable (String executable) {
        this.executable = executable;
    }

    public String getTargetDir () {
        return targetDir;
    }

    public void setTargetDir (String targetDir) {
        this.targetDir = targetDir;
    }

    public String getServer () {
        return server;
    }

    public void setServer (String server) {
        this.server = server;
    }

    public int getPort () {
        return port;
    }

    public void setPort (int port) {
        this.port = port;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getUserPassword () {
        return userPassword;
    }

    public void setUserPassword (String userPassword) {
        this.userPassword = userPassword;
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
}
