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

public class PageUri {

    private String[] path;

    private final String uri;

    public PageUri(String uri) {
        this.uri = uri;
        if (uri != null) {
            path = uri.split(":");
        }
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
        if (!(obj instanceof PageUri)) {
            return false;
        }
        PageUri that = (PageUri) obj;
        if (this.uri.equals(that.uri)) {
            return true;
        } else {
            return false;
        }
    }

    public String getUri() {
        return uri;
    }

    public boolean isContained(PageUri parent) {
        return uri.startsWith(parent.getUri());
    }

    public boolean isRoot() {
        return !uri.contains(":");
    }
}
