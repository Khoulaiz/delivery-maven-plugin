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

import java.util.HashMap;
import java.util.List;

import com.sahlbach.maven.delivery.DeliveryMojo;
import com.sahlbach.maven.delivery.Exec;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * User: Andreas Sahlbach
 * Date: 20.08.11
 * Time: 21:28
 */
public abstract class Executor {

    private static HashMap<String,Class<? extends Executor>> executorMap = new HashMap<String, Class<? extends Executor>>();

    private Log logger;

    static {
        executorMap.put("ssh", SshExecutor.class);
//        uploaderMap.put("ftp", FtpExecutor.class);
    }

    public static Executor createExecutor(String type, Log logger) {
        Class<? extends Executor> executorClass = executorMap.get(type);
        Executor executor = null;
        if(executorClass != null) {
            try {
                executor = executorClass.newInstance();
                executor.setLogger(logger);
            } catch (InstantiationException e) {
                logger.error("Can't create Executor of type "+ type);
            } catch (IllegalAccessException e) {
                logger.error("Can't create Executor of type " + type);
            }
        }
        return executor;
    }

    public abstract void execute (List<String> commands, Exec exec, DeliveryMojo mojo) throws
        MojoFailureException, MojoExecutionException;

    public Log getLogger () {
        return logger;
    }

    public void setLogger (Log logger) {
        this.logger = logger;
    }

}
