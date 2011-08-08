package com.sahlbach.maven.delivery.upload;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sahlbach.maven.delivery.Upload;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Map;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 23:02
 */
public class ScpUploader extends Uploader {

    @Override
    public void uploadFiles (Map<File,String> filesToUpload, String targetDir, Upload upload) throws MojoFailureException {

        Session session = null;
        try {
            UserInfo userInfo = new UserInfo(upload.getUsername(),upload.getUserPassword());
            String host = upload.getServer();
            int port = upload.getPort() == 0 ? 22 : upload.getPort();

            JSch jsch = new JSch();
            session = jsch.getSession(userInfo.getUser(), host, port);
            session.setUserInfo(userInfo);

            session.connect();
            Scp scp = new Scp(session);

            for (Map.Entry<File, String> copyEntry : filesToUpload.entrySet()) {
                getLogger().debug("Delivering file " + copyEntry.getKey().getAbsolutePath());
                scp.put(copyEntry.getKey().getAbsolutePath(),targetDir,copyEntry.getValue(), upload.getFileMask());
                if(getLogger().isDebugEnabled()) {
                    getLogger().info("Delivered: " + copyEntry.getKey().getAbsolutePath()+" to " + host + ":"
                                     + targetDir + "/"+copyEntry.getValue());
                } else {
                    getLogger().info("Delivered: " + copyEntry.getKey().getName());
                }
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
