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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ClassicThemePanel extends SimplePanel {

    private final ClassicThemeProperties property;

    private AbsolutePanel headerPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    private Image logoImage;

    public ClassicThemePanel(ClassicThemeProperties property) {
        this.property = property;
        add(createContentPanel());
        getElement().getStyle().setProperty("background", property.background);

        createHeaderCaptions();

        createLogoImage();

    }

    protected Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");
        style.setPaddingTop(property.contentPanelTopMargin, Unit.PX);
        style.setPaddingBottom(property.contentPanelBottomMargin, Unit.PX);

        contentPanel.setWidth(property.contentPanelWidth + "px");

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);

        contentPanel.add(createMainPanel());

        footerPanel = createFooterPanel();
        contentPanel.add(footerPanel);

        return contentPanel;
    }

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.getElement().getStyle().setProperty("background", property.headerBackground);
        headerPanel.setHeight(property.headerHeight + "px");

        return headerPanel;
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.remove(w);
        headerPanel.add(w, left, top);
    }

    protected AbsolutePanel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();

        footerPanel.getElement().getStyle().setProperty("background", property.footerBackground);
        footerPanel.setHeight(property.footerHeight + "px");

        return footerPanel;
    }

    public void addToFooterPanel(Widget w, int left, int top) {
        footerPanel.add(w, left, top);
    }

    Panel createMainPanel() {
        SimplePanel mainPanel = new SimplePanel();

        Style style = mainPanel.getElement().getStyle();

        style.setPadding(20, Unit.PX);

        style.setProperty("background", property.mainPanelBackground);

        mainPanel
                .setWidget(new HTML(
                        "MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel MainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanelMainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>MainPanel<p>",
                        true));

        return mainPanel;
    }

    protected void createHeaderCaptions() {
        headerCaptions = new Label();

        headerCaptions.getElement().getStyle().setColor(property.headerCaptionsColor);
        headerCaptions.getElement().getStyle().setFontSize(property.headerCaptionsFontSize, Unit.PX);
    }

    public void setHeaderCaptions(String captions) {
        headerCaptions.setText(captions);
        addToHeaderPanel(headerCaptions, property.headerCaptionsLeft, property.headerCaptionsTop);
    }

    protected void createLogoImage() {
        logoImage = new Image();
    }

    public void setLogoImage(String url) {
        logoImage.setUrl(url);
        addToHeaderPanel(logoImage, 20, 20);

    }

}
