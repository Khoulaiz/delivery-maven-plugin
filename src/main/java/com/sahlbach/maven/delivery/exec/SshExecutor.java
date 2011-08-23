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

package com.sahlbach.maven.delivery.exec;

import java.io.InputStream;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.sahlbach.maven.delivery.DeliveryMojo;
import com.sahlbach.maven.delivery.Exec;
import com.sahlbach.maven.delivery.ssh.SshHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;

/**
 * User: Andreas Sahlbach
 * Date: 20.08.11
 * Time: 21:34
 */
public class SshExecutor extends Executor {

    @Override
    public void execute (List<String> commands, Exec exec, DeliveryMojo mojo)
        throws MojoFailureException, MojoExecutionException {
        if (StringUtils.isNotEmpty(exec.getExecutable())) {
            externalExec(commands, exec, mojo);
        } else {
            internalExec(commands, exec, mojo);
        }
    }

    private void internalExec (List<String> commands, Exec exec, DeliveryMojo mojo) throws MojoFailureException {
        Session session = null;
        try {
            session = SshHelper.connectSsh(exec, mojo);

            Channel channel = session.openChannel("exec");
            StringBuilder remoteCmdLine = new StringBuilder();

            for (String command : commands) {
                remoteCmdLine.append(command).append(exec.getCommandSeparator());
            }
            ((ChannelExec)channel).setCommand(remoteCmdLine.toString());

            if(mojo.isInteractiveMode()) {
                channel.setInputStream(System.in);
            } else {
                channel.setInputStream(null);
            }
            channel.setOutputStream(System.out);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            if (getLogger().isInfoEnabled()) {
                getLogger().info("Executing: " + remoteCmdLine.toString());
            }
            channel.connect();

            byte[] tmp = new byte[1024];
            while(true) {
                while(in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if(i < 0)
                        break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    if(getLogger().isDebugEnabled())
                        getLogger().debug("Exit Status: "+channel.getExitStatus());
                    break;
                }
                try{
                    Thread.sleep(1000);
                }catch(Exception ee){
                    getLogger().error(ee);
                }
            }
            channel.disconnect();
            if (getLogger().isDebugEnabled()) {
                getLogger().info("Executed: " + remoteCmdLine);
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

    private void externalExec (List<String> commands, Exec exec, DeliveryMojo mojo) throws MojoExecutionException {
        StringBuilder remoteCmdLine = new StringBuilder();

        for (String command : commands) {
            remoteCmdLine.append(command).append(exec.getCommandSeparator());
        }
        try {
            Commandline cmd = new Commandline();

            cmd.setExecutable(exec.getExecutable());

            if (exec.getPort() != 0) {
                cmd.createArg().setValue("-p");
                cmd.createArg().setValue(Integer.toString(exec.getPort()));
            }

            cmd.createArg().setValue(exec.getUsername()+"@"+exec.getServer());
            cmd.createArg().setValue(remoteCmdLine.toString());

            if(getLogger().isInfoEnabled())
                getLogger().info("Executing: "+cmd);
            int exitCode = CommandLineUtils.executeCommandLine(cmd, null, new DefaultConsumer(), new DefaultConsumer());

            if ( exitCode == 255 ) {
                throw new MojoExecutionException( "Exit code: " + exitCode );
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Executed: " + cmd);
            }
        }
        catch ( CommandLineException e ) {
            throw new MojoExecutionException( "Unable to execute command", e );
        }
    }
}
