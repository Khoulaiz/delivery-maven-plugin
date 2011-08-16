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
