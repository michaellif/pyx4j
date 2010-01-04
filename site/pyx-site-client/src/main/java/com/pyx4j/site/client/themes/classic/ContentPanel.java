/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

public class ContentPanel extends FlowPanel {

    public ContentPanel() {
        getElement().getStyle().setProperty("marginLeft", "auto");
        getElement().getStyle().setProperty("marginRight", "auto");
        getElement().getStyle().setPaddingTop(20, Unit.PX);
        getElement().getStyle().setPaddingBottom(20, Unit.PX);

        setWidth("968px");

        add(new HeaderPanel());
        add(new MainPanel());
        add(new FooterPanel());
    }

}
