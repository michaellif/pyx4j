/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

/**
 * TODO remove in GWT 2.0.1 since klass.getSimpleName() should be implemented then.
 */
public class GWTJava5Helper {

    public static String getSimpleName(Class<?> klass) {
        // Java 1.5
        // klass.getSimpleName()
        String simpleName = klass.getName();
        // strip the package name
        return simpleName.substring(simpleName.lastIndexOf(".") + 1);
    }

    //TODO Use Java 1.7 Long.compare
    public static int longCompare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static boolean isAssignableFrom(Class<?> child, Class<?> cls) {
        if (cls == null) {
            return false;
        }

        if (cls.equals(child)) {
            return true;
        }

        Class<?> currentSuperClass = cls.getSuperclass();
        while (currentSuperClass != null) {
            if (currentSuperClass.equals(child)) {
                return true;
            }
            currentSuperClass = currentSuperClass.getSuperclass();
        }
        return false;
    }
}
