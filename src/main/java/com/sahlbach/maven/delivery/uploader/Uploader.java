package com.sahlbach.maven.delivery.uploader;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

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

    public static Uploader createUploader(String scheme, Log logger) {
        Class<? extends Uploader> uploaderClass = uploaderMap.get(scheme.toLowerCase());
        Uploader uploader = null;
        if(uploaderClass != null) {
            try {
                uploader = uploaderClass.newInstance();
                uploader.setLogger(logger);
            } catch (InstantiationException e) {
                logger.error("Can't find Uploader for scheme "+scheme);
            } catch (IllegalAccessException e) {
                logger.error("Can't find Uploader for scheme " + scheme);
            }
        }
        return uploader;
    }

    public abstract void uploadFiles (List<File> filesToUpload, URI target) throws MojoFailureException;

    public Log getLogger () {
        return logger;
    }

    public void setLogger (Log logger) {
        this.logger = logger;
    }
}
