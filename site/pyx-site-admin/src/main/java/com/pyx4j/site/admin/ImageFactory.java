/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.admin;

import com.google.gwt.core.client.GWT;

public class ImageFactory {

    private static AdminImageBundle bundle = GWT.create(AdminImageBundle.class);

    public static AdminImageBundle getImages() {
        return bundle;
    }

}
