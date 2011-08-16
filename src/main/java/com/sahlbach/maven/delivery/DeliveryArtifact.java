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

/**
 * Author: Andreas Sahlbach
 * Date: 05.08.11
 * Time: 21:14
 */
public class DeliveryArtifact {

    /**
     * group id of the artifact
     *
     * @parameter
     */
    private String groupId;

    /**
     * artifact id of the artifact
     *
     * @parameter
     */
    private String artifactId;

    /**
     * extension of the artifact
     *
     * @parameter
     */
    private String extension;

    /**
     * version of the artifact
     *
     * @parameter
     */
    private String version;

    /**
     * classifier of the artifact
     *
     * @parameter
     */
    private String classifier;

    public String getGroupId () {
        return groupId;
    }

    public void setGroupId (String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId () {
        return artifactId;
    }

    public void setArtifactId (String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public String getExtension () {
        return extension;
    }

    public void setExtension (String extension) {
        this.extension = extension;
    }

    public String getClassifier () {
        return classifier;
    }

    public void setClassifier (String classifier) {
        this.classifier = classifier;
    }

    @Override
    public String toString () {
        StringBuilder str = new StringBuilder();
        if(groupId != null) {
            str.append(groupId).append(":");
        }
        if(artifactId != null) {
            str.append(artifactId);
        }
        if(extension != null) {
            str.append(":").append(extension);
        }
        if(classifier != null) {
            str.append(":").append(classifier);
        }
        if(version != null) {
            str.append(":").append(version);
        }
        return str.toString();
    }
}
