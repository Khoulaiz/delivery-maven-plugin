package com.sahlbach.maven.delivery.upload;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sahlbach.maven.delivery.Upload;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.List;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 23:02
 */
public class ScpUploader extends Uploader {

    @Override
    public void uploadFiles (List<File> filesToUpload, String targetDir, Upload upload) throws MojoFailureException {

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

            for (File file : filesToUpload) {
                getLogger().debug("Delivering file "+file.getAbsolutePath());
                scp.put(file.getAbsolutePath(),targetDir,file.getName(), upload.getFileMask());
                if(getLogger().isDebugEnabled()) {
                    getLogger().info("Delivered: "+file.getAbsolutePath()+" to "+host+":"+targetDir);
                } else {
                    getLogger().info("Delivered: "+file.getName());
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
