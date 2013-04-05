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
 * Created on 2011-02-09
 * @author antonk
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.site.shared.meta.NavigNode;

public class AppPlaceInfo {

    private final String navigLabel;

    private final String caption;

    private final String staticContent;

    public String getNavigLabel() {
        return navigLabel;
    }

    public String getCaption() {
        return caption;
    }

    public String getResource() {
        return staticContent;
    }

    public AppPlaceInfo(String navigLabel, String caption, String staticContent) {
        this.navigLabel = navigLabel != null && !navigLabel.equals("") ? navigLabel : null;
        this.caption = caption != null && !caption.equals("") ? caption : null;
        this.staticContent = staticContent != null && !staticContent.equals("") ? staticContent : null;
    }

    public static String getPlaceId(Class<? extends Place> clazz) {
        String simpleName = clazz.getName();
        // strip the package name
        simpleName = simpleName.substring(simpleName.indexOf("$") + 1).replace("$", "/");

        StringBuilder builder = new StringBuilder();
        char[] charArray = simpleName.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (i == 0) {
                builder.append(Character.toLowerCase(charArray[i]));
            } else if (Character.isUpperCase(charArray[i])) {
                if (charArray[i - 1] != '/') {
                    builder.append('_');
                }
                builder.append(Character.toLowerCase(charArray[i]));
            } else {
                builder.append(charArray[i]);
            }
        }
        return builder.toString();
    }

    public static IDebugId getPlaceIDebugId(Place place) {
        return getPlaceIDebugId(place.getClass());
    }

    public static IDebugId getPlaceIDebugId(Class<? extends Place> clazz) {
        return new StringDebugId(getPlaceId(clazz));
    }

    public static String absoluteUrl(String appUrl, boolean redirectable, AppPlace place) {
        StringBuilder b = new StringBuilder();
        if (appUrl != null) {
            b.append(appUrl);
        }
        if (redirectable) {
            b.append("?").append(NavigNode.PLACE_ARGUMENT).append("=");
        } else {
            b.append("#");
        }
        b.append(place.getToken(redirectable));
        return b.toString();
    }

    public static String absoluteUrl(String appUrl, boolean redirectable, Class<? extends Place> placeClass, String... encodedComponentsNameValue) {
        StringBuilder b = new StringBuilder();
        if (appUrl != null) {
            b.append(appUrl);
        }
        if (placeClass != null) {
            if (redirectable) {
                b.append("?").append(NavigNode.PLACE_ARGUMENT).append("=");
            } else {
                b.append("#");
            }
            b.append(getPlaceId(placeClass));
        }
        if (encodedComponentsNameValue != null) {
            boolean first = !redirectable;
            boolean name = true;
            for (String encodedComponent : encodedComponentsNameValue) {
                if (first) {
                    b.append(NavigNode.ARGS_GROUP_SEPARATOR);
                    first = false;
                } else if (name) {
                    b.append(NavigNode.ARGS_SEPARATOR);
                } else {
                    b.append(NavigNode.NAME_VALUE_SEPARATOR);
                }
                name = !name;
                b.append(encodedComponent);
            }
        }
        return b.toString();
    }
}