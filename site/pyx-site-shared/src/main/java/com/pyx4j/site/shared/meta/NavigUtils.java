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
 * Created on May 21, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.shared.meta;

import static com.pyx4j.site.shared.meta.NavigNode.ARGS_GROUP_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.ARGS_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.NAME_VALUE_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.PAGE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

public class NavigUtils {

    public static final String ENTITY_ID = "id";

    public static final String PARENT_ID = "pid";

    public static String getPageUri(Class<? extends NavigNode> node) {
        String[] parts = node.getName().split("\\$");

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            builder.append(parts[i].toLowerCase());
            if (i < parts.length - 1) {
                builder.append(PAGE_SEPARATOR);
            }
        }
        // This is site
        if (parts.length == 2) {
            // Assume home page of site
            builder.append(PAGE_SEPARATOR);
        }
        return builder.toString();
    }

    public static String getSiteId(Class<? extends NavigNode> node) {
        String[] parts = node.getName().split("\\$");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Node is not a site node");
        }
        return parts[1].toLowerCase();
    }

    /**
     * Warning: it does not encode Component!
     */
    public static String absoluteUrl(String appUrl, Class<? extends NavigNode> node, String... encodedComponentsNameValue) {
        StringBuilder b = new StringBuilder();
        b.append(appUrl);

        if (node != null) {
            b.append("#");
            b.append(NavigUtils.getPageUri(node));
        }

        if (encodedComponentsNameValue != null) {
            boolean first = true;
            boolean name = true;
            for (String encodedComponent : encodedComponentsNameValue) {
                if (first) {
                    b.append(ARGS_GROUP_SEPARATOR);
                    first = false;
                } else if (name) {
                    b.append(ARGS_SEPARATOR);
                } else {
                    b.append(NAME_VALUE_SEPARATOR);
                }
                name = !name;
                b.append(encodedComponent);
            }
        }
        return b.toString();
    }

    public static List<String> parseResourceUri(String uri) {
        List<String> path = new ArrayList<String>();
        String[] parts = uri.split("\\" + PAGE_SEPARATOR);
        for (String string : parts) {
            path.add(string);
        }
        return path;
    }

    public static boolean isContained(String parent, String child) {
        if (parent == null || child == null) {
            return false;
        }
        return child.equals(parent) || child.startsWith(parent + PAGE_SEPARATOR);
    }

    @Deprecated
    /**
     * @Deprecated use EqualsHelper
     */
    public static boolean areEqual(String uri1, String uri2) {
        if (uri1 == null || uri2 == null) {
            return false;
        }
        return uri1 != null && uri1.equals(uri2);
    }

    public static boolean isRoot(String uri) {
        return parseResourceUri(uri).size() == 2;
    }

    public static String getRoot(String uri) {
        List<String> path = parseResourceUri(uri);
        return path.get(0) + PAGE_SEPARATOR + path.get(1);
    }

    public static String getParent(String uri) {
        int lastItemIndex = uri.lastIndexOf(PAGE_SEPARATOR);
        return uri.substring(0, lastItemIndex);
    }

}
