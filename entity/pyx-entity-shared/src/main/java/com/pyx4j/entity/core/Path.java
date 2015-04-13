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
 */
package com.pyx4j.entity.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.ICloneable;
import com.pyx4j.commons.IDebugId;

public class Path implements Serializable, IDebugId, ICloneable {

    private static final long serialVersionUID = -1723967141846287126L;

    public static final char PATH_SEPARATOR = '/';

    public static final String COLLECTION_SEPARATOR = "[]";

    private String path;

    private transient String rootObjectClassName;

    private transient Class<? extends IEntity> rootEntityClass;

    private transient List<String> pathMembers;

    protected Path() {
    }

    @Deprecated
    public Path(String path) {
        this.path = path;
    }

    public Path(Class<? extends IEntity> entityClass, String memberName) {
        this.rootEntityClass = entityClass;
        this.path = GWTJava5Helper.getSimpleName(entityClass) + PATH_SEPARATOR + memberName + PATH_SEPARATOR;
    }

    public Path(Class<? extends IEntity> entityClass, List<String> pathMembers) {
        this.rootEntityClass = entityClass;
        this.rootObjectClassName = GWTJava5Helper.getSimpleName(entityClass);
        this.pathMembers = pathMembers;
        this.path = rootObjectClassName + PATH_SEPARATOR;
        for (String pathMember : pathMembers) {
            path += pathMember + Path.PATH_SEPARATOR;
        }
    }

    public Path(Path path, List<String> pathMembers) {
        this(path.getRootEntityClass(), concat(path.getPathMembers(), pathMembers));
    }

    public Path(IObject<?> object) {
        path = "";
        List<String> members = new Vector<String>();
        while (object != null) {
            String pathElement = null;
            if (object.getParent() instanceof ICollection) {
                pathElement = COLLECTION_SEPARATOR;
            } else if (object.getFieldName() == null) {
                this.rootEntityClass = ((IEntity) object).getInstanceValueClass();
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

    private static List<String> concat(List<String> pathMembers1, List<String> pathMembers2) {
        List<String> r = new ArrayList<>(pathMembers1);
        r.addAll(pathMembers2);
        return r;
    }

    public boolean isUndefinedCollectionPath() {
        getPathMembers();
        int p = 0;
        for (String pm : pathMembers) {
            if (pm.equals(COLLECTION_SEPARATOR)) {
                return p < pathMembers.size() - 1;
            }
            p++;
        }
        return false;
    }

    private void parsPath() {
        int rootIdx = path.indexOf(PATH_SEPARATOR);
        if (rootIdx < 1) {
            throw new IllegalArgumentException(path);
        }
        rootObjectClassName = path.substring(0, rootIdx);

        String membersString = path.substring(rootIdx + 1);
        String[] members = membersString.split(String.valueOf(PATH_SEPARATOR));

        pathMembers = Collections.unmodifiableList(Arrays.asList(members));
    }

    //This does not work after obfuscated class names in GWT!
    public String getRootObjectClassName() {
        if (rootObjectClassName == null) {
            parsPath();
        }
        return rootObjectClassName;
    }

    public Class<? extends IEntity> getRootEntityClass() {
        assert rootEntityClass != null : "Can't access EntityClass after serialization";
        return rootEntityClass;
    }

    public List<String> getPathMembers() {
        if (pathMembers == null) {
            parsPath();
        }
        return pathMembers;
    }

    @Override
    public boolean equals(Object other) {
        // This assert will be commented out after migration
        assert (other == null) || other instanceof Path : "Do not try to compare Path to " + other.getClass().getSimpleName();
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

    /**
     * The same change as happening in serialization
     */
    @Override
    public Path iclone() {
        Path p = new Path();
        p.path = this.path;
        return p;
    }

    @Override
    public String debugId() {
        return path.substring(0, path.length() - 1).replace(PATH_SEPARATOR, DEBUGID_SEPARATOR);
    }

}
