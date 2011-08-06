package com.sahlbach.maven.delivery.uploader;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 23:02
 */
public class ScpUploader extends Uploader {

    @Override
    public void uploadFiles (List<File> filesToUpload, URI target) throws MojoFailureException {

        Session session = null;
        try {
            UserInfo userInfo = new UserInfo(target.getUserInfo());
            String host = target.getHost();
            String path = target.getPath();
            int port = target.getPort() == -1 ? 22 : target.getPort();

            JSch jsch = new JSch();
            session = jsch.getSession(userInfo.getUser(), host, port);
            session.setUserInfo(userInfo);

            session.connect();
            Scp scp = new Scp(session);

            for (File file : filesToUpload) {
                scp.put(file.getAbsolutePath(),path,file.getName(),"0644");
            }

            session.disconnect();
            session = null;
        } catch(Exception e){
            throw new MojoFailureException("SCP failed.",e);
        } finally {
            if(session != null)
                session.disconnect();
        }
    }

}
