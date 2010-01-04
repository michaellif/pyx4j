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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class MainPanel extends SimplePanel {
    public MainPanel() {
        getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Unit.PX);
        getElement().getStyle().setBorderColor("green");
        getElement().getStyle().setBackgroundColor("white");

        setWidget(new HTML(
                "MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>",
                true));

    }
}
