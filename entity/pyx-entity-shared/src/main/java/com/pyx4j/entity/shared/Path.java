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

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.GWTJava5Helper;

public class Path {

    private String path = "";

    private String rootObjectClassName;

    private final List<String> pathMembers;

    //    public Path(String path) {
    //        //TODO the path parsing.
    //    }

    public Path(IObject<?> object) {
        List<String> members = new Vector<String>();
        while (object != null) {
            String pathElement = null;
            if (object.getParent() instanceof ICollection) {
                pathElement = "[]";
            } else if (object.getFieldName() == null) {
                rootObjectClassName = GWTJava5Helper.getSimpleName(object.getObjectClass());
                this.path = rootObjectClassName + "/" + this.path;
            } else {
                pathElement = object.getFieldName();

            }
            if (pathElement != null) {
                this.path = pathElement + "/" + this.path;
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

    public String getRootObjectClassName() {
        return rootObjectClassName;
    }

    public List<String> getPathMembers() {
        return pathMembers;
    }

    @Override
    public String toString() {
        return path;
    }

}
