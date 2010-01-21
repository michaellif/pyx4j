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
 * Created on Jan 13, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class ResourceUri {

    public static String SITE_SEPARATOR = ":";

    public static String PAGE_SEPARATOR = "/";

    private String siteName;

    private String[] path;

    private final String uri;

    public ResourceUri(String uri) {
        this.uri = uri;
        if (uri != null) {
            String[] components = uri.split(SITE_SEPARATOR);
            if (components.length == 2) {
                siteName = components[0];
                path = components[1].split(PAGE_SEPARATOR);
            } else {
                throw new RuntimeException("Wrong URI format " + uri);
            }
        }
    }

    public ResourceUri(String siteName, String... path) {
        this.uri = createUri(siteName, path);
        this.siteName = siteName;
        this.path = path;
    }

    @Override
    public String toString() {
        return uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ResourceUri)) {
            return false;
        }
        ResourceUri that = (ResourceUri) obj;
        if (this.uri.equals(that.uri)) {
            return true;
        } else {
            return false;
        }
    }

    public String getUri() {
        return uri;
    }

    public String getSiteName() {
        return siteName;
    }

    public boolean isContained(ResourceUri parent) {
        return uri.startsWith(parent.getUri());
    }

    public boolean isRoot() {
        return !uri.contains(PAGE_SEPARATOR);
    }

    public static String createUri(String siteName, String... path) {
        StringBuilder builder = new StringBuilder();
        builder.append(siteName);
        builder.append(SITE_SEPARATOR);
        for (int i = 0; i < path.length;) {
            builder.append(path[i]);
            if (++i != path.length)
                builder.append(PAGE_SEPARATOR);
        }
        return builder.toString();
    }
}
