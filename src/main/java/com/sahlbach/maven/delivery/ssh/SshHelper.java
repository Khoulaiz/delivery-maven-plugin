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

package com.sahlbach.maven.delivery.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sahlbach.maven.delivery.AbstractSshRemoteJob;
import com.sahlbach.maven.delivery.DeliveryMojo;
import com.sahlbach.maven.delivery.upload.UserInfo;

public class SshHelper {

    private SshHelper(){}

    public static Session connectSsh(AbstractSshRemoteJob job, DeliveryMojo mojo) throws JSchException {
        UserInfo userInfo = new UserInfo(job, mojo.getLog(), mojo.isInteractiveMode() ? mojo.getPrompter() : null);
        int port = job.getPort() == 0 ? 22 : job.getPort();

        JSch jsch = new JSch();
        Session session = jsch.getSession(userInfo.getUser(), job.getServer(), port);
        session.setUserInfo(userInfo);

        session.connect();
        return session;
    }
}
