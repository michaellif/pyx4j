/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.core.client.GWT;

public class ImageFactory {

    private static WidgetsImageBundle bundle = GWT.create(WidgetsImageBundle.class);

    public static WidgetsImageBundle getImages() {
        return bundle;
    }

}
