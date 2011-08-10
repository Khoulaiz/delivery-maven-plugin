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

public class UserInfo implements com.jcraft.jsch.UserInfo {

    String user;
    String password;

    public UserInfo(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public UserInfo(String userInfo) {
        String tmp[] = userInfo.split(":");
        user = tmp[0];
        if(tmp.length > 0) {
            password = tmp[1];
        }
    }

    public String getPassphrase () {
        return null;
    }

    public String getPassword () {
        return password;
    }

    public boolean promptPassword (String message) {
        return true;
    }

    public boolean promptPassphrase (String message) {
        return true;
    }

    public boolean promptYesNo (String message) {
        return true;
    }

    public void showMessage (String message) {}

    public String getUser () {
        return user;
    }
}