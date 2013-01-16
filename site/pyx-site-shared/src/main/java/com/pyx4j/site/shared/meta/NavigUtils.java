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

import static com.pyx4j.site.shared.meta.NavigNode.PAGE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.ClassName;

public class NavigUtils {

    public static String getPageUri(Class<? extends NavigNode> node) {
        String[] parts = ClassName.getClassName(node).split("\\$");

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
        String[] parts = ClassName.getClassName(node).split("\\$");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Node is not a site node");
        }
        return parts[1].toLowerCase();
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
