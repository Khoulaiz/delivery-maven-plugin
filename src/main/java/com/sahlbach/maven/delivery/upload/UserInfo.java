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