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
