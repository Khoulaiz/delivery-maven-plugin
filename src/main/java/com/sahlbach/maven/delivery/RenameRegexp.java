package com.sahlbach.maven.delivery;

import java.util.regex.Pattern;

/**
 * User: Andreas Sahlbach
 * Date: 08.08.11
 * Time: 21:39
 */
public class RenameRegexp {

    /**
     * Java Regular Expression that has to match the filename to rename it.
     * @parameter
     * @required
     */
    private String from;

    private Pattern fromPattern;

    /**
     * Replacement that is used if the regexp matches. You can use java capturing groups for replacement.
     * @parameter
     * @required
     */
    private String to;

    public String getFrom () {
        return from;
    }

    public void setFrom (String from) {
        this.from = from;
        this.fromPattern = Pattern.compile(from);
    }

    public String getTo () {
        return to;
    }

    public void setTo (String to) {
        this.to = to;
    }

    public Pattern getFromPattern () {
        return fromPattern;
    }
}
