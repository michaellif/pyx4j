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
 * Created on Jan 26, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.shared.util;

import static com.pyx4j.site.shared.domain.ResourceUri.PAGE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.shared.domain.ResourceUri;

public class ResourceUriUtil {

    public static ResourceUri createResourceUri(String siteName, String... path) {
        StringBuilder builder = new StringBuilder();
        builder.append(siteName);
        builder.append(PAGE_SEPARATOR);
        if (path != null) {
            for (int i = 0; i < path.length;) {
                builder.append(path[i]);
                if (++i < path.length) {
                    builder.append(PAGE_SEPARATOR);
                }
            }
        }

        ResourceUri resourceUri = EntityFactory.create(ResourceUri.class);
        resourceUri.uri().setValue(builder.toString());

        return resourceUri;
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

    public static boolean areEqual(String uri1, String uri2) {
        if (uri1 == null || uri2 == null) {
            return false;
        }
        return uri1 != null && uri1.equals(uri2);
    }

    public static boolean isRoot(String uri) {
        return parseResourceUri(uri).size() == 2;
    }

    public static ResourceUri getRoot(String uri) {
        List<String> path = parseResourceUri(uri);
        return createResourceUri(path.get(0), path.get(1));
    }

    public static ResourceUri getParent(String uri) {
        List<String> path = parseResourceUri(uri);
        String[] subpath = new String[path.size() - 2];
        for (int i = 1; i < path.size() - 1; i++) {
            subpath[i - 1] = path.get(i);
        }

        return createResourceUri(path.get(0), subpath);
    }

}
