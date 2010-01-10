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

public class Path {

    private String path = "";

    public Path(IObject<?, ?> object) {
        while (object != null) {
            if (object.getFieldName() == null) {
                this.path = getSimpleName(object.getObjectClass()) + "/" + this.path;
            } else {
                this.path = object.getFieldName() + "/" + this.path;
            }
            object = object.getParent();
        }

    }

    @Override
    public String toString() {
        return path;
    }

    /**
     * TODO remove in GWT 2.0.1 since klass.getSimpleName() should be implemented then.
     */
    private static String getSimpleName(Class<?> klass) {
        // Java 1.5
        // klass.getSimpleName()
        String simpleName = klass.getName();
        // strip the package name
        return simpleName.substring(simpleName.lastIndexOf(".") + 1);
    }

}
