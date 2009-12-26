/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.images;

import com.google.gwt.core.client.GWT;

public class ImageFactory {

    private static ImageBundle bundle;

    public static ImageBundle getImages() {
        if (bundle == null) {
            bundle = GWT.create(ImageBundle.class);
        }
        return bundle;
    }

    /**
     * Allow implementation to create single super Bundle.
     */
    public static void setImageBundle(ImageBundle bundle) {
        if (ImageFactory.bundle != null) {
            //TODO
            System.out.println("WARNING: AppImageBundle already loaded");
        }
        ImageFactory.bundle = bundle;
    }

}
