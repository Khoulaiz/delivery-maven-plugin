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

package com.sahlbach.maven.delivery.prompt;

import com.google.common.collect.MapMaker;

import java.util.Map;

/**
 * User: Andreas Sahlbach
 * Date: 22.09.11
 * Time: 20:53
 */
public class PasswordCache {
    /**
     * will be used as cache for entered passwords
     */
    private static Map<String,String> passwordCache = new MapMaker().makeMap();
    private static final String SEPARATOR = "@";

    public void put(String user, String server, String password) {
        passwordCache.put(createKey(user, server),password);
    }

    public void put(String message,String password) {
        passwordCache.put(message,password);
    }

    public boolean contains(String user, String server) {
        return passwordCache.containsKey(createKey(user, server));
    }

    public boolean contains(String message) {
        return passwordCache.containsKey(message);
    }

    public String get(String user, String server) {
        return passwordCache.get(createKey(user, server));
    }

    public String get(String message) {
        return passwordCache.get(message);
    }

    private String createKey (String user, String server) {
        return user.toLowerCase()+SEPARATOR+server.toLowerCase();
    }
}
