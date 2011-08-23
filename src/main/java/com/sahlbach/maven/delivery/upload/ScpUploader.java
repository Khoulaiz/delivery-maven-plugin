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

package com.sahlbach.maven.delivery.upload;

import java.io.File;
import java.util.Map;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sahlbach.maven.delivery.DeliveryMojo;
import com.sahlbach.maven.delivery.Upload;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 23:02
 */
public class ScpUploader extends Uploader {

    @Override
    public void uploadFiles (Map<File, String> filesToUpload, Upload upload, DeliveryMojo mojo) throws MojoFailureException, MojoExecutionException {

        if (StringUtils.isNotEmpty(upload.getExecutable())) {
            externalUpload(filesToUpload, upload, mojo);
        } else {
            internalUpload(filesToUpload, upload, mojo);
        }
    }

    private void internalUpload (Map<File, String> filesToUpload, Upload upload, DeliveryMojo mojo) throws MojoFailureException {
        Session session = null;
        try {
            UserInfo userInfo = new UserInfo(upload.getUsername(),
                                             upload.getUserPassword(),
                                             upload.getKeyPassword(),
                                             mojo.getLog(),
                                             mojo.isInteractiveMode() ? mojo.getPrompter() : null);
            String host = upload.getServer();
            int port = upload.getPort() == 0 ? 22 : upload.getPort();

            JSch jsch = new JSch();
            session = jsch.getSession(userInfo.getUser(), host, port);
            session.setUserInfo(userInfo);

            session.connect();
            Scp scp = new Scp(session);

            for (Map.Entry<File, String> copyEntry : filesToUpload.entrySet()) {
                getLogger().debug("Delivering file " + copyEntry.getKey().getAbsolutePath());
                scp.put(copyEntry.getKey().getAbsolutePath(), upload.getTargetDir(), copyEntry.getValue(), upload.getFileMask());
                if (getLogger().isDebugEnabled()) {
                    getLogger().info("Delivered: " + copyEntry.getKey().getAbsolutePath() + " to " + host + ":"
                                     + upload.getTargetDir() + "/" + copyEntry.getValue());
                } else {
                    getLogger().info("Delivered: " + copyEntry.getKey().getName());
                }
            }

            session.disconnect();
            session = null;
        } catch (Exception e) {
            throw new MojoFailureException("SCP failed.", e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void externalUpload (Map<File, String> filesToUpload, Upload upload, DeliveryMojo mojo) throws MojoFailureException, MojoExecutionException {
        for (Map.Entry<File, String> copyEntry : filesToUpload.entrySet()) {
            getLogger().debug("Delivering file " + copyEntry.getKey().getAbsolutePath());
            Commandline cmd = new Commandline();

            cmd.setExecutable(upload.getExecutable());

            if (upload.getPort() != 0) {
                cmd.createArg().setValue("-P");
                cmd.createArg().setValue(Integer.toString(upload.getPort()));
            }

            if(upload.getKeyfile() != null) {
                cmd.createArg().setValue("-i");
                cmd.createArg().setFile(upload.getKeyfile());
            }

            cmd.createArg().setFile(copyEntry.getKey());
            StringBuilder targetArg = new StringBuilder();
            if(upload.getUsername() != null)
                targetArg.append(upload.getUsername()).append("@");
            targetArg.append(upload.getServer()).append(":");
            if(upload.getTargetDir() != null) {
                targetArg.append(upload.getTargetDir()).append("/");
            }
            targetArg.append(copyEntry.getValue());
            cmd.createArg().setValue(targetArg.toString());

            try {
                getLogger().debug("Executing: "+cmd);
                int exitCode = CommandLineUtils.executeCommandLine(cmd, null, new DefaultConsumer(), new DefaultConsumer());

                if ( exitCode != 0 ) {
                    throw new MojoExecutionException( "Exit code: " + exitCode );
                }
	        }
	        catch ( CommandLineException e ) {
	            throw new MojoExecutionException( "Unable to execute command", e );
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().info(
                    "Delivered: " + copyEntry.getKey().getAbsolutePath() + " to " + upload.getServer() + ":"
                    + upload.getTargetDir() + "/" + copyEntry.getValue());
            } else {
                getLogger().info("Delivered: " + copyEntry.getKey().getName());
            }
        }
    }
}
