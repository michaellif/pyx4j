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

import static com.pyx4j.site.shared.meta.NavigNode.PAGE_SEPARATOR;

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

}
