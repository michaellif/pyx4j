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
import static com.pyx4j.site.shared.domain.ResourceUri.SITE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.shared.domain.ResourceUri;

public class ResourceUriUtil {

    public static ResourceUri createResourceUri(String siteName, String... path) {
        StringBuilder builder = new StringBuilder();
        builder.append(siteName);
        builder.append(SITE_SEPARATOR);
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

    public static List<String> parseResourceUri(ResourceUri uri) {
        List<String> path = new ArrayList<String>();
        String uriStr = uri.uri().getValue();
        String[] siteAndPages = uriStr.split("\\" + SITE_SEPARATOR);
        path.add(siteAndPages[0]);
        if (siteAndPages[1].contains(PAGE_SEPARATOR)) {
            String[] pages = siteAndPages[1].split("\\" + PAGE_SEPARATOR);
            for (String string : pages) {
                path.add(string);
            }
        } else {
            path.add(siteAndPages[1]);
        }
        return path;
    }

    public static boolean isContained(ResourceUri parent, ResourceUri child) {
        if (parent == null || child == null) {
            return false;
        }
        String childValue = child.uri().getValue();
        String parentValue = parent.uri().getValue();

        return childValue.equals(parentValue) || childValue.startsWith(parentValue + PAGE_SEPARATOR);
    }

    public static boolean areEqual(ResourceUri uri1, ResourceUri uri2) {
        if (uri1 == null || uri2 == null) {
            return false;
        }
        String uri1Value = uri1.uri().getValue();
        String uri2Value = uri2.uri().getValue();

        return uri1Value != null && uri1Value.equals(uri2Value);
    }

    public static boolean isRoot(ResourceUri uri) {
        return parseResourceUri(uri).size() == 2;
    }

    public static ResourceUri getRoot(ResourceUri uri) {
        List<String> path = parseResourceUri(uri);
        return createResourceUri(path.get(0), path.get(1));
    }

    public static ResourceUri getParent(ResourceUri uri) {
        List<String> path = parseResourceUri(uri);
        String[] subpath = new String[path.size() - 2];
        for (int i = 1; i < path.size() - 1; i++) {
            subpath[i - 1] = path.get(i);
        }

        return createResourceUri(path.get(0), subpath);
    }

}
