package com.sahlbach.maven.delivery;

import java.net.URI;
import java.util.List;

/**
 * @author Andreas Sahlbach
 *         Date: 8/3/11
 *         Time: 5:18 PM
 */
public class Delivery {

    /**
     * Target URI of this delivery;
     * @parameter expression="file:///${project.output.dir}"
     * @required
     */
    private URI target;

    /**
     * A list of artifactCoords like this {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>}
     *
     * @parameter
     */
    private List<DeliveryArtifact> artifacts;


    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }

    public List<DeliveryArtifact> getArtifacts () {
        return artifacts;
    }

    public void setArtifacts (List<DeliveryArtifact> artifacts) {
        this.artifacts = artifacts;
    }
}
