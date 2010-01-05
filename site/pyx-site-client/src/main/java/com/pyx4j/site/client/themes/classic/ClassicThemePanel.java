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
import com.google.gwt.user.client.ui.Widget;

public class ClassicThemePanel extends SimplePanel {

    private final ClassicThemeProperty property;

    private AbsolutePanel headerPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    public ClassicThemePanel(ClassicThemeProperty property) {
        this.property = property;
        add(createContentPanel());
        getElement().getStyle().setProperty("background", property.getBackground());

        createHeaderCaptions();

    }

    protected Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");
        style.setPaddingTop(property.getContentPanelTopMargin(), Unit.PX);
        style.setPaddingBottom(property.getContentPanelBottomMargin(), Unit.PX);

        contentPanel.setWidth(property.getContentPanelWidth() + "px");

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);

        contentPanel.add(createMainPanel());

        footerPanel = createFooterPanel();
        contentPanel.add(footerPanel);

        return contentPanel;
    }

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.getElement().getStyle().setProperty("background", property.getHeaderBackground());
        headerPanel.setHeight(property.getHeaderHeight() + "px");

        return headerPanel;
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.add(w, left, top);
    }

    protected AbsolutePanel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();

        footerPanel.getElement().getStyle().setProperty("background", property.getFooterBackground());
        footerPanel.setHeight(property.getFooterHeight() + "px");

        return footerPanel;
    }

    public void addToFooterPanel(Widget w, int left, int top) {
        footerPanel.add(w, left, top);
    }

    Panel createMainPanel() {
        SimplePanel mainPanel = new SimplePanel();

        Style style = mainPanel.getElement().getStyle();

        style.setPadding(20, Unit.PX);

        style.setProperty("background", property.getMainPanelBackground());

        mainPanel
                .setWidget(new HTML(
                        "MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>",
                        true));

        return mainPanel;
    }

    protected void createHeaderCaptions() {
        headerCaptions = new Label();
        headerCaptions.getElement().getStyle().setColor("yellow");
        headerCaptions.getElement().getStyle().setFontSize(28, Unit.PX);
        addToHeaderPanel(headerCaptions, property.getHeaderCaptionsLeft(), property.getHeaderCaptionsTop());
    }

    public void setHeaderCaptions(String captions) {
        headerCaptions.setText(captions);
    }

}
