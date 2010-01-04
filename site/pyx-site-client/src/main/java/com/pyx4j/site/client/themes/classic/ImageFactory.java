/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.themes.classic.images.ClassicThemeSkin1Images;

public class ImageFactory {

    private static ClassicThemeSkin1Images bundle = GWT.create(ClassicThemeSkin1Images.class);

    public static ClassicThemeSkin1Images getImages() {
        return bundle;
    }

}
