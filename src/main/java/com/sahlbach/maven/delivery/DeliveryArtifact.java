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
