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
 * Created on Jan 12, 2010
 * @author vlads
 */
package com.pyx4j.security.shared;

import com.pyx4j.commons.GWTSerializable;

public class BasicPermission implements Permission {

    private static final long serialVersionUID = 7407387043880019023L;

    //Not final because of GWT
    @GWTSerializable
    private boolean wildcard;

    //Not final because of GWT
    @GWTSerializable
    private String path;

    public BasicPermission(String path) {
        this.wildcard = path.endsWith("*");
        if (this.wildcard) {
            path = path.substring(0, path.length() - 1);
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getActions() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getPath());
        String a = getActions();
        if ((a != null) && (a.length() > 0)) {
            b.append(' ');
            b.append(a);
        }
        return b.toString();
    }

    @Override
    public boolean implies(Permission p) {
        if ((p == null) || (p.getClass() != getClass())) {
            return false;
        }
        BasicPermission other = (BasicPermission) p;
        if (this.wildcard) {
            if (other.wildcard) {
                // one wildcard can imply another
                return other.path.startsWith(path);
            } else {
                // make sure ap.path is longer so a.b.* doesn't imply a.b
                return (other.path.length() > this.path.length()) && other.path.startsWith(this.path);
            }
        } else {
            if (other.wildcard) {
                // a non-wildcard can't imply a wildcard
                return false;
            } else {
                return this.path.equals(other.path);
            }
        }
    }

    @GWTSerializable
    @Deprecated
    private void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }

    @GWTSerializable
    @Deprecated
    private void setPath(String path) {
        this.path = path;
    }

}
