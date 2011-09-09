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

public abstract class AbstractRemoteJob {
    /**
     * hostname of the target server
     * @parameter
     */
    private String server;

    /**
     * port of the target server
     * @parameter
     */
    private int port = 0;

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

    public AbstractRemoteJob mergeWith(AbstractRemoteJob remoteJob) {
        if(remoteJob.getServer() != null)
            server = remoteJob.getServer();
        if(remoteJob.getPort() != 0)
            port = remoteJob.getPort();
        if(remoteJob.getUsername() != null)
            username = remoteJob.getUsername();
        if(remoteJob.getUserPassword() != null)
            userPassword = remoteJob.getUserPassword();
        return this;
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
}
