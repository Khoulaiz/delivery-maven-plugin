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

import com.sahlbach.maven.delivery.Exec;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

/**
 * User: Andreas Sahlbach
 * Date: 20.08.11
 * Time: 21:34
 */
public class SshExecutor extends Executor{
    @Override
    public void execute (List<String> commands, String targetPath, Exec exec)
        throws MojoFailureException, MojoExecutionException {
        if (StringUtils.isNotEmpty(exec.getExecutable())) {
            externalExec(commands, targetPath, exec);
        } else {
            internalExec(commands, targetPath, exec);
        }
    }

    private void internalExec (List<String> commands, String targetPath, Exec exec) {
        // TODO: implement

    }

    private void externalExec (List<String> commands, String targetPath, Exec exec) {
        // TODO: implement

    }
}
