/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes.classic;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PagePanel extends SimplePanel {

    public PagePanel() {

        add(createContentPanel());

        getElement().getStyle().setBackgroundColor("#F8F8F8");

        //        getElement().getStyle().setBackgroundColor("#21262C");
        //        getElement().getStyle().setBackgroundImage("url('images/background.jpg')");
        //        getElement().getStyle().setProperty("backgroundRepeat", "repeat-x");
    }

    Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");
        style.setPaddingTop(20, Unit.PX);
        style.setPaddingBottom(20, Unit.PX);

        contentPanel.setWidth("968px");

        contentPanel.add(createHeaderPanel());
        contentPanel.add(createMainPanel());
        contentPanel.add(createFooterPanel());

        return contentPanel;
    }

    Panel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();
        headerPanel.add(new Label("HeaderPanel"), 40, 40);
        headerPanel.add(new Label("HeaderPanel"), 45, 45);

        headerPanel.getElement().getStyle().setBackgroundImage("url('images/container-header.gif')");
        headerPanel.getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");

        headerPanel.setHeight("200px");

        return headerPanel;
    }

    Panel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();
        footerPanel.add(new Label("FooterPanel"), 40, 40);

        footerPanel.getElement().getStyle().setProperty("background", "url('images/container-footer.gif') no-repeat 50% 100%");

        footerPanel.setHeight("100px");

        return footerPanel;
    }

    Panel createMainPanel() {
        SimplePanel mainPanel = new SimplePanel();

        Style style = mainPanel.getElement().getStyle();

        style.setPadding(20, Unit.PX);

        style.setBackgroundImage("url('images/container-main.gif')");
        style.setProperty("backgroundRepeat", "repeat-y");

        mainPanel
                .setWidget(new HTML(
                        "MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>",
                        true));

        return mainPanel;
    }
}
