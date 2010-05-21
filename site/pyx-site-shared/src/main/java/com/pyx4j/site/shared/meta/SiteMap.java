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

public class SiteMap {

    public static String PATH_SEPARATOR = "/";

    public static String ARGS_GROUP_SEPARATOR = "?";

    public static String ARGS_SEPARATOR = "&";

    public static String NAME_VALUE_SEPARATOR = "=";

    public static String getPageUri(Class<? extends NavigNode> page) {
        String[] parts = page.getName().split("\\$");

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            builder.append(parts[i]);
            if (i < parts.length - 1) {
                builder.append(PATH_SEPARATOR);
            }
        }
        return builder.toString();
    }

}
