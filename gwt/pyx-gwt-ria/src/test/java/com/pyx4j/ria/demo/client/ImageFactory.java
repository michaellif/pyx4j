/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.demo.client;

import com.google.gwt.core.client.GWT;

public class ImageFactory {

    private static RiaDemoImageBundle bundle = GWT.create(RiaDemoImageBundle.class);

    public static RiaDemoImageBundle getImages() {
        return bundle;
    }

}
