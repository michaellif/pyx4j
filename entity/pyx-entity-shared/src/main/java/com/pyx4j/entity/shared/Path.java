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
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IDebugId;

public class Path implements Serializable, IDebugId {

    private static final long serialVersionUID = -1723967141846287126L;

    public static final char PATH_SEPARATOR = '/';

    public static final String COLLECTION_SEPARATOR = "[]";

    private String path;

    private transient String rootObjectClassName;

    private transient List<String> pathMembers;

    protected Path() {

    }

    public Path(String path) {
        this.path = path;
    }

    public Path(IObject<?> object) {
        path = "";
        List<String> members = new Vector<String>();
        while (object != null) {
            String pathElement = null;
            if (object.getParent() instanceof ICollection) {
                pathElement = COLLECTION_SEPARATOR;
            } else if (object.getFieldName() == null) {
                rootObjectClassName = GWTJava5Helper.getSimpleName(object.getObjectClass());
                this.path = rootObjectClassName + PATH_SEPARATOR + this.path;
            } else {
                pathElement = object.getFieldName();

            }
            if (pathElement != null) {
                this.path = pathElement + PATH_SEPARATOR + this.path;
                if (members.size() == 0) {
                    members.add(pathElement);
                } else {
                    members.add(0, pathElement);
                }
            }
            object = object.getParent();
        }
        pathMembers = Collections.unmodifiableList(members);
    }

    private void parsPath() {
        List<String> members = new Vector<String>();
        int rootIdx = path.indexOf(PATH_SEPARATOR);
        if (rootIdx < 1) {
            throw new IllegalArgumentException(path);
        }
        rootObjectClassName = path.substring(0, rootIdx);
        int memberIdx = rootIdx + 1;
        while (memberIdx > 0) {
            int memberIdxEnd = path.indexOf(PATH_SEPARATOR, memberIdx);
            if (memberIdxEnd > 0) {
                members.add(path.substring(memberIdx, memberIdxEnd));
                memberIdx = memberIdxEnd + 1;
            } else {
                break;
            }
        }
        pathMembers = Collections.unmodifiableList(members);
    }

    //This does not work after RPC!
    @Deprecated
    public String getRootObjectClassName() {
        if (rootObjectClassName == null) {
            parsPath();
        }
        return rootObjectClassName;
    }

    public List<String> getPathMembers() {
        if (pathMembers == null) {
            parsPath();
        }
        return pathMembers;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Path)) {
            return false;
        }
        return path.equals(((Path) other).path);
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public String debugId() {
        return path.substring(0, path.length() - 1).replace(PATH_SEPARATOR, '$');
    }

}
