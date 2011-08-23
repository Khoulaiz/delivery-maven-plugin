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

}
