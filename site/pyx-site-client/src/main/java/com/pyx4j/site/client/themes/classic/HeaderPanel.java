/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends AbsolutePanel {
    public HeaderPanel() {
        getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Unit.PX);
        getElement().getStyle().setBorderColor("green");
        getElement().getStyle().setBackgroundColor("white");

        add(new Label("HeaderPanel"), 40, 40);
        add(new Label("HeaderPanel"), 45, 45);

        setHeight("200px");
    }
}
