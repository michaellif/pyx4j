/**
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
}
