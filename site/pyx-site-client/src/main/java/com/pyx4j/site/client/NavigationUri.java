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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

import com.pyx4j.site.shared.meta.NavigNode;
import com.pyx4j.site.shared.meta.NavigUtils;

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

    public NavigationUri(Class<? extends NavigNode> page) {
        setPath(NavigUtils.getPageUri(page));
    }

    public NavigationUri(Class<? extends NavigNode> page, String name, String value) {
        setPath(NavigUtils.getPageUri(page));
        addArg(name, value);
    }

    public NavigationUri(Class<? extends NavigNode> page, String name1, String value1, String name2, String value2) {
        setPath(NavigUtils.getPageUri(page));
        addArg(name1, value1);
        addArg(name2, value2);
    }

    public static Map<String, String> parsArgs(String queryString) {
        if (queryString.startsWith(NavigNode.ARGS_GROUP_SEPARATOR)) {
            queryString = queryString.substring(1);
        }
        Map<String, String> args = new HashMap<>();
        if (queryString.length() == 0) {
            return args;
        }
        String[] nameValues = queryString.split(NavigNode.ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(NavigNode.NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], URL.decodeComponent(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    /**
     * Used for inter-modules redirections.
     * Consider http://localhost:8888/gwt/ and http://localhost:8888/gwt/index.html
     */
    public static String getDeploymentBaseURL() {
        String url = GWT.getModuleBaseURL();
        String module = GWT.getModuleName();
        if (url.endsWith(module + "/")) {
            url = url.substring(0, url.length() - (module.length() + 1));
        }
        return url;
    }

    public static String getHostPageURL() {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setProtocol(Window.Location.getProtocol());
        urlBuilder.setHost(Window.Location.getHost());
        String path = Window.Location.getPath();
        if (path != null && path.length() > 0) {
            urlBuilder.setPath(path);
        }
        return urlBuilder.buildString();
    }

    public String getPath() {
        if (path == null) {
            StringBuilder newToken = new StringBuilder();
            newToken.append(pageUri);

            boolean first = true;
            for (Map.Entry<String, String> me : args.entrySet()) {
                if (first) {
                    newToken.append(NavigNode.ARGS_GROUP_SEPARATOR);
                    first = false;
                } else {
                    newToken.append(NavigNode.ARGS_SEPARATOR);
                }
                newToken.append(me.getKey());
                newToken.append(NavigNode.NAME_VALUE_SEPARATOR);
                newToken.append(URL.encodeComponent(me.getValue()));
            }
            path = newToken.toString();
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        int splitIndex = path.indexOf(NavigNode.ARGS_GROUP_SEPARATOR);
        if (splitIndex == -1) {
            pageUri = path.toLowerCase();
        } else {
            pageUri = path.substring(0, splitIndex).toLowerCase();
            if (path.length() > splitIndex) {
                args = parsArgs(path.substring(splitIndex + 1));
            }
        }
        if (args == null) {
            args = new HashMap<>();
        }
        int siteIndex = pageUri.indexOf(NavigNode.PAGE_SEPARATOR);
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
        int siteIndex = pageUri.indexOf(NavigNode.PAGE_SEPARATOR);
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
        if (args == null) {
            args = new HashMap<>();
        }
        this.args.put(name, value);
        path = null;
    }

    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof NavigationUri) && getPath().equals(((NavigationUri) other).getPath());
    }

}