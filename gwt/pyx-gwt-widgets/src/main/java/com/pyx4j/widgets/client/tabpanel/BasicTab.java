/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class BasicTab implements ITab {

    private final Widget contentPane;

    private final AbstractImagePrototype imagePrototype;

    private final String title;

    public BasicTab(Widget contentPane, String title, AbstractImagePrototype imagePrototype) {
        super();
        this.contentPane = contentPane;
        this.imagePrototype = imagePrototype;
        this.title = title;
    }

    @Override
    public Widget getContentPane() {
        return contentPane;
    }

    @Override
    public AbstractImagePrototype getImagePrototype() {
        return imagePrototype;
    }

    @Override
    public String getTitle() {
        return title;
    }

}
