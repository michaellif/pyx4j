/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 4, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import static com.pyx4j.site.shared.meta.NavigNode.ARGS_GROUP_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.ARGS_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.NAME_VALUE_SEPARATOR;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;

public class AppPlace extends Place {

    private static final Logger log = LoggerFactory.getLogger(AppPlace.class);

    private Map<String, String> args;

    public AppPlace() {
    }

    public void putArg(String key, String value) {
        if (args == null) {
            args = new HashMap<String, String>();
        }
        this.args.put(key, value);
    }

    public void putAllArgs(Map<String, String> map) {
        if (args == null) {
            args = new HashMap<String, String>();
        }
        this.args.putAll(map);
    }

    public void parseArgs(String queryString) {
        if (args == null) {
            args = new HashMap<String, String>();
        }
        args.putAll(parseQueryString(queryString));
    }

    public Map<String, String> getArgs() {
        return Collections.unmodifiableMap(args);
    }

    public String getArg(String key) {
        if (args != null) {
            return args.get(key);
        } else {
            return null;
        }
    }

    public String getToken() {
        return AppPlaceInfo.getPlaceId(getClass()) + createQueryString();
    }

    @Override
    public boolean equals(Object other) {
        if (getClass() == other.getClass()) {
            if (args == null && ((AppPlace) other).args == null) {
                return true;
            } else if (args != null && args.equals(((AppPlace) other).args)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        if (args != null) {
            hash = hash * 31 + args.hashCode();
        }
        return hash;
    }

    protected String createQueryString() {
        if (args == null) {
            return "";
        }
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> me : args.entrySet()) {
            if (first) {
                queryString.append(ARGS_GROUP_SEPARATOR);
                first = false;
            } else {
                queryString.append(ARGS_SEPARATOR);
            }
            queryString.append(me.getKey());
            queryString.append(NAME_VALUE_SEPARATOR);
            queryString.append(URL.encodeQueryString(me.getValue()));
        }
        return queryString.toString();
    }

    protected static Map<String, String> parseQueryString(String queryString) {
        if (queryString.startsWith(ARGS_GROUP_SEPARATOR)) {
            queryString = queryString.substring(1);
        }
        Map<String, String> args = new HashMap<String, String>();
        if (queryString.length() == 0) {
            return args;
        }
        String[] nameValues = queryString.split(ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], URL.decodeQueryString(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + createQueryString();
    }
}
