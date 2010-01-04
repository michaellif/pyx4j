/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends AbsolutePanel {
    public HeaderPanel() {
        add(new Label("HeaderPanel"), 40, 40);
        add(new Label("HeaderPanel"), 45, 45);

        getElement().getStyle().setBackgroundImage("url('images/container-header.gif')");
        getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");

        setHeight("200px");
    }
}
