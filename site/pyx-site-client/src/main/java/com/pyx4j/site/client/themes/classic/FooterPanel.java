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

public class FooterPanel extends AbsolutePanel {
    public FooterPanel() {

        add(new Label("FooterPanel"), 40, 40);

        getElement().getStyle().setProperty("background", "url('images/container-footer.gif') no-repeat 50% 100%");

        setHeight("100px");
    }
}
