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

import com.sahlbach.maven.delivery.AbstractSshRemoteJob;
import com.sahlbach.maven.delivery.prompt.PasswordCache;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.util.Arrays;

public class UserInfo implements com.jcraft.jsch.UserInfo {

    private String user;
    private String password;
    private String passphrase;
    private String server;
    private Prompter prompter;
    private Log logger;
    private StreamConsumer consumer = new DefaultConsumer();
    private static PasswordCache passwordCache = new PasswordCache();

    public UserInfo (AbstractSshRemoteJob sshJob, Log logger, Prompter prompter) {
        this.user = sshJob.getUsername();
        this.password = sshJob.getUserPassword();
        this.passphrase = sshJob.getKeyPassword();
        this.server = sshJob.getServer();
        this.prompter = prompter;
        this.logger = logger;
    }

    public String getPassphrase () {
        return passphrase;
    }

    public String getPassword () {
        return password;
    }

    public boolean promptPassword (String message) {
        if(password == null && passwordCache.contains(message)) {
            this.password = passwordCache.get(message);
        }
        if(password == null && prompter != null) {
            try {
                password = prompter.promptForPassword(message);
                passwordCache.put(message,password);
            } catch (PrompterException e) {
                logger.error(e);
            }
        }
        return true;
    }

    public boolean promptPassphrase (String message) {
        if(password == null && passwordCache.contains(message)) {
            this.password = passwordCache.get(message);
        }
        if(passphrase == null && prompter != null) {
            try {
                passphrase = prompter.promptForPassword(message);
                passwordCache.put(message,password);
            } catch (PrompterException e) {
                logger.error(e);
            }
        }
        return true;
    }

    public boolean promptYesNo (String message) {
        String yesno = null;
        if(prompter != null) {
            try {
                yesno = prompter.prompt(message, Arrays.asList("yes","no"));
            } catch (PrompterException e) {
                logger.error(e);
            }
        }
        return (yesno == null) || ("yes".equalsIgnoreCase(yesno));
    }

    public void showMessage (String message) {
        consumer.consumeLine(message);
    }

    public String getUser () {
        return user;
    }
}