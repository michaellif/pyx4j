/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

public class Path {

    private String path = "";

    public Path(IEntityHandler<?> handler) {
        while (handler != null) {
            if (handler.getFieldName() == null) {
                this.path = getSimpleName(handler.getEntityClass()) + "/" + this.path;
            } else {
                this.path = handler.getFieldName() + "/" + this.path;
            }
            handler = handler.getParentHandler();
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
