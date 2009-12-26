/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public class BasicTab implements ITab {

    private final Widget contentPane;

    private final ImageResource imageResource;

    private final String title;

    public BasicTab(Widget contentPane, String title, ImageResource imageResource) {
        super();
        this.contentPane = contentPane;
        this.imageResource = imageResource;
        this.title = title;
    }

    @Override
    public Widget getContentPane() {
        return contentPane;
    }

    @Override
    public ImageResource getImageResource() {
        return imageResource;
    }

    @Override
    public String getTitle() {
        return title;
    }

}
