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

import java.util.List;

import com.sahlbach.maven.delivery.exec.Executor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * User: Andreas Sahlbach
 * Date: 06.08.11
 * Time: 11:24
 */
public class Exec extends AbstractSshRemoteJob {

    public final static String DEFAULT_TYPE = "ssh";
    /**
     * List of commands to execute
     * @parameter
     * @required
     */
    private List<String> commands;

    /**
     * String to use on remote system to concatenate the commands together
     * @parameter
     * @required
     */
    private String commandSeparator = ";";

    public void execute (DeliveryMojo mojo) throws MojoExecutionException, MojoFailureException {
        String type = getType() != null ? getType() : DEFAULT_TYPE;
        Executor executor = Executor.createExecutor(type.toLowerCase(), mojo.getLog());
        if(executor == null) {
            throw new MojoExecutionException("Can't find Executor of type " + getType());
        }
        executor.execute(commands, this, mojo);
    }

    public Exec mergeWith(Exec exec) throws MojoExecutionException {
        super.mergeWith(exec);
        if(exec.getCommands() != null)
            setCommands(exec.getCommands());
        if(exec.getCommandSeparator() != null)
            setCommandSeparator(exec.getCommandSeparator());
        return this;
    }

    public List<String> getCommands () {
        return commands;
    }

    public void setCommands (List<String> commands) {
        this.commands = commands;
    }

    public String getCommandSeparator() {
        return commandSeparator;
    }

    public void setCommandSeparator(String commandSeparator) {
        this.commandSeparator = commandSeparator;
    }
}
