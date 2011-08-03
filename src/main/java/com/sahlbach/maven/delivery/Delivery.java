package com.sahlbach.maven.delivery;

import java.net.URL;

/**
 * Copyright by Volkswagen AG
 *
 * @author Andreas Sahlbach
 *         Date: 8/3/11
 *         Time: 5:18 PM
 */
public class Delivery {

    /**
     * Target URL of this delivery;
     * @parameter expression="${project.output.dir}"
     * @required
     */
    private URL target;


    public URL getTarget() {
        return target;
    }

    public void setTarget(URL target) {
        this.target = target;
    }
}
