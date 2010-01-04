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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class MainPanel extends SimplePanel {
    public MainPanel() {
        getElement().getStyle().setPadding(20, Unit.PX);

        getElement().getStyle().setBackgroundImage("url('images/container-main.gif')");
        getElement().getStyle().setProperty("backgroundRepeat", "repeat-y");

        setWidget(new HTML(
                "MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>",
                true));

    }
}
