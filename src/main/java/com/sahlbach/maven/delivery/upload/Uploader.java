package com.sahlbach.maven.delivery.upload;

import com.sahlbach.maven.delivery.Upload;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 22:56
 */
public abstract class Uploader {

    private static HashMap<String,Class<? extends Uploader>> uploaderMap = new HashMap<String, Class<? extends Uploader>>();

    private Log logger;

    static {
        uploaderMap.put("scp", ScpUploader.class);
//        uploaderMap.put("sftp://", SftUploader.class);
//        uploaderMap.put("http://", HttpUploader.class);
//        uploaderMap.put("https://", HttpsUploader.class);
//        uploaderMap.put("ftp://", FtpUploader.class);
//        uploaderMap.put("file://", FileUploader.class);
    }

    public static Uploader createUploader(String type, Log logger) {
        Class<? extends Uploader> uploaderClass = uploaderMap.get(type);
        Uploader uploader = null;
        if(uploaderClass != null) {
            try {
                uploader = uploaderClass.newInstance();
                uploader.setLogger(logger);
            } catch (InstantiationException e) {
                logger.error("Can't create Uploader of type "+ type);
            } catch (IllegalAccessException e) {
                logger.error("Can't create Uploader of type " + type);
            }
        }
        return uploader;
    }

    public abstract void uploadFiles (Map<File,String> filesToUpload, String targetPath, Upload upload) throws MojoFailureException;

    public Log getLogger () {
        return logger;
    }

    public void setLogger (Log logger) {
        this.logger = logger;
    }
}
