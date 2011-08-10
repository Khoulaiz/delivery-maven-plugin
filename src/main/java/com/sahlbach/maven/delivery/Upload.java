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

package com.sahlbach.maven.delivery;

import com.sahlbach.maven.delivery.upload.Uploader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * User: Andreas Sahlbach
 * Date: 06.08.11
 * Time: 11:24
 */
public class Upload {

    /**
     * Type of upload
     * @parameter
     * @required
     */
    private String type;

    /**
     * target directory on the remote server
     * @parameter
     * @required
     */
    private String targetDir;

    /**
     * local fileset to upload
     * @parameter
     */
    private Fileset fileset;

    /**
     * list of artifacts to upload. artifacts will be fetched from repository first
     * @parameter
     */
    private List<DeliveryArtifact> artifacts;

    /**
     * hostname of the target server
     * @parameter
     */
    private String server;

    /**
     * port of the target server
     * @parameter 
     */
    private int port;

    /**
     * remote username to use to login
     * @parameter
     */
    private String username;

    /**
     * password to use for username / password authentication
     * @parameter
     */
    private String userPassword;

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

    /**
     * File mask to use for copied files. Default is 0644
     * @parameter default-value="0644"
     */
    private String fileMask;

    /**
     * Optional list of regexp to rename files during copy. The first regexp that matches will be used to rename the
     * file. The file path is excluding of this operation and the regexp match attempt.
     * @parameter
     */
    private List<RenameRegexp> renameRegexps;

    public void execute (DeliveryMojo mojo) throws MojoExecutionException, MojoFailureException {
        Uploader uploader = Uploader.createUploader(type.toLowerCase(), mojo.getLog());
        if(uploader == null) {
            throw new MojoExecutionException("Can't find Uploader of type " + type);
        }
        List<File> filesToUpload = resolveFileset();

        if(artifacts != null)
            filesToUpload.addAll(resolveArtifacts(mojo));

        Map<File,String> filesWithTargetNames = calculateTagetNames(filesToUpload);

        uploader.uploadFiles(filesWithTargetNames, targetDir,this);
    }

    private Map<File,String> calculateTagetNames (List<File> filesToUpload) {
        Map<File,String> filesWithTargetNames = new HashMap<File, String>(filesToUpload.size());
        for (File file : filesToUpload) {
            for (RenameRegexp renameRegexp : renameRegexps) {
                Matcher matcher = renameRegexp.getFromPattern().matcher(file.getName());
                if(matcher.matches()) {
                    filesWithTargetNames.put(file,matcher.replaceAll(renameRegexp.getTo()));
                    break;
                } else {
                    filesWithTargetNames.put(file,file.getName());
                }
            }
        }
        return filesWithTargetNames;
    }

    public List<File> resolveArtifacts(DeliveryMojo mojo) throws MojoFailureException, MojoExecutionException {

        List<File> resultList = new ArrayList<File>(artifacts.size());

        for (DeliveryArtifact deliveryArtifact : artifacts) {
            Artifact artifact;
            try {
                artifact = new DefaultArtifact( deliveryArtifact.toString() );
            }
            catch ( IllegalArgumentException e ) {
                throw new MojoFailureException( e.getMessage(), e );
            }
            ArtifactRequest request = new ArtifactRequest();
            request.setArtifact( artifact );
            request.setRepositories( mojo.getRemoteRepos() );

            mojo.getLog().debug( "Resolving artifact " + artifact + " from " + mojo.getRemoteRepos() );

            ArtifactResult result;
            try {
                result = mojo.getRepoSystem().resolveArtifact(mojo.getRepoSession(), request);
            }
            catch ( ArtifactResolutionException e ) {
                throw new MojoExecutionException( e.getMessage(), e );
            }
            mojo.getLog().info( "Resolved artifact " + artifact + " to " + result.getArtifact().getFile() + " from "
                                + result.getRepository() );
            resultList.add(result.getArtifact().getFile());
        }
        return resultList;
    }

    private List<File> resolveFileset () {
        String[] includedFiles = new String[0];
        if ( fileset != null ) {
            fileset.scan();
            includedFiles = fileset.getIncludedFiles();
        }
        List<File> result = new ArrayList<File>(includedFiles.length);
        for (String includedFile : includedFiles) {
            result.add(new File(fileset.getBasedir(), includedFile));
        }
        return result;
    }

    public int getPort () {
        return port;
    }

    public String getUsername () {
        return username;
    }

    public String getUserPassword () {
        return userPassword;
    }

    public File getKeyfile () {
        return keyfile;
    }

    public String getKeyPassword () {
        return keyPassword;
    }

    public String getType () {
        return type;
    }

    public String getTargetDir () {
        return targetDir;
    }

    public Fileset getFileset () {
        return fileset;
    }

    public List<DeliveryArtifact> getArtifacts () {
        return artifacts;
    }

    public String getFileMask () {
        return fileMask;
    }

    public String getServer () {
        return server;
    }

    public List<RenameRegexp> getRenameRegexps () {
        return renameRegexps;
    }

    public void setRenameRegexps (List<RenameRegexp> renameRegexps) {
        this.renameRegexps = renameRegexps;
    }
}
