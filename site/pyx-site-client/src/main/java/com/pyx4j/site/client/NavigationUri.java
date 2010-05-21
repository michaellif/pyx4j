/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-14
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.http.client.URL;

import com.pyx4j.site.shared.domain.ResourceUri;
import com.pyx4j.site.shared.util.PageTypeUriEnum;

public class NavigationUri {

    private static final Logger log = LoggerFactory.getLogger(NavigationUri.class);

    private String path;

    private String siteName;

    private String pageUri;

    private Map<String, String> args;

    /**
     * Split path to uri and arguments
     * 
     * @param path
     */
    public NavigationUri(String path) {
        setPath(path);
    }

    public NavigationUri(PageTypeUriEnum pageTypeUri) {
        setPath(pageTypeUri.getUri().uri().getValue());
    }

    public static Map<String, String> parsArgs(String substring) {
        Map<String, String> args = new HashMap<String, String>();
        String[] nameValues = substring.split(ResourceUri.ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(ResourceUri.NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], URL.decode(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    public String getPath() {
        if (path == null) {
            StringBuilder newToken = new StringBuilder();
            newToken.append(pageUri);

            boolean first = true;
            for (Map.Entry<String, String> me : args.entrySet()) {
                if (first) {
                    newToken.append(ResourceUri.ARGS_GROUP_SEPARATOR);
                    first = false;
                } else {
                    newToken.append(ResourceUri.ARGS_SEPARATOR);
                }
                newToken.append(me.getKey());
                newToken.append(ResourceUri.NAME_VALUE_SEPARATOR);
                newToken.append(URL.encode(me.getValue()));
            }
            path = newToken.toString();
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        int splitIndex = path.indexOf(ResourceUri.ARGS_GROUP_SEPARATOR);
        if (splitIndex == -1) {
            pageUri = path;
        } else {
            pageUri = path.substring(0, splitIndex);
            if (path.length() > splitIndex) {
                args = parsArgs(path.substring(splitIndex + 1));
            }
        }
        if (args == null) {
            args = new HashMap<String, String>();
        }
        int siteIndex = pageUri.indexOf(ResourceUri.PAGE_SEPARATOR);
        if (siteIndex > 0) {
            siteName = pageUri.substring(0, siteIndex);
        } else {
            siteName = "";
        }
    }

    public String getSiteName() {
        return siteName;
    }

    public String getPageUri() {
        return pageUri;
    }

    public void setPageUri(String pageUri) {
        this.pageUri = pageUri;
        int siteIndex = pageUri.indexOf(ResourceUri.PAGE_SEPARATOR);
        if (siteIndex > 0) {
            siteName = pageUri.substring(0, siteIndex);
        } else {
            siteName = "";
        }
        path = null;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        if (args == null) {
            this.args.clear();
        } else {
            this.args = args;
        }
        path = null;
    }

    public void addArg(String name, String value) {
        this.args.put(name, value);
        path = null;
    }

}
