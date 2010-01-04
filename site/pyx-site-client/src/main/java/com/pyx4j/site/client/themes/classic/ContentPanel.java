/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.user.client.ui.FlowPanel;

public class ContentPanel extends FlowPanel {

    public ContentPanel() {
        getElement().getStyle().setProperty("marginLeft", "auto");
        getElement().getStyle().setProperty("marginRight", "auto");
        getElement().getStyle().setProperty("paddingTop", "20px");
        getElement().getStyle().setProperty("paddingBottom", "20px");

        setWidth("700px");

        add(new HeaderPanel());
        add(new MainPanel());
        add(new FooterPanel());
    }

}
