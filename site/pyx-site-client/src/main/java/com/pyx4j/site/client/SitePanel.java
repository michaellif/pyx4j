/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.domain.Page;
import com.pyx4j.site.client.domain.Site;

public class SitePanel extends SimplePanel implements ValueChangeHandler<String>, ResizeHandler {

    private final Site site;

    private AbsolutePanel headerPanel;

    private SimplePanel mainPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    private Image logoImage;

    public SitePanel(Site site) {
        this.site = site;

        setSize("100%", "100%");
        Window.addResizeHandler(this);

        add(createContentPanel());
        getElement().getStyle().setProperty("background", site.properties.background);

        createHeaderCaptions();

        createLogoImage(site.logoUrl);

        History.addValueChangeHandler(this);

        // Show the initial screen.
        String initToken = History.getToken();
        if (initToken.length() > 0) {
            Page page = site.getPage(initToken);
            if (page == null) {
                show(site.getHomePage());
            } else {
                show(page);
            }
        } else {
            show(site.getHomePage());
        }

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        onResize(null);
    }

    private void show(Page page) {
        System.out.println("Show page " + page);

        mainPanel.setWidget(new HTML(page.data.html, true));

        setHeaderCaptions(page.caption);

    }

    protected Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");
        style.setPaddingTop(site.properties.contentPanelTopMargin, Unit.PX);
        style.setPaddingBottom(site.properties.contentPanelBottomMargin, Unit.PX);

        contentPanel.setWidth(site.properties.contentPanelWidth + "px");

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);

        mainPanel = createMainPanel();
        contentPanel.add(mainPanel);

        footerPanel = createFooterPanel();
        contentPanel.add(footerPanel);

        return contentPanel;
    }

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.getElement().getStyle().setProperty("background", site.properties.headerBackground);
        headerPanel.setHeight(site.properties.headerHeight + "px");

        return headerPanel;
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.remove(w);
        headerPanel.add(w, left, top);
    }

    protected AbsolutePanel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();

        footerPanel.getElement().getStyle().setProperty("background", site.properties.footerBackground);
        footerPanel.setHeight(site.properties.footerHeight + "px");

        return footerPanel;
    }

    public void addToFooterPanel(Widget w, int left, int top) {
        footerPanel.add(w, left, top);
    }

    SimplePanel createMainPanel() {
        SimplePanel mainPanel = new SimplePanel();

        Style style = mainPanel.getElement().getStyle();

        style.setPadding(20, Unit.PX);

        style.setProperty("background", site.properties.mainPanelBackground);

        return mainPanel;
    }

    protected void createHeaderCaptions() {
        headerCaptions = new Label();

        headerCaptions.getElement().getStyle().setColor(site.properties.headerCaptionsColor);
        headerCaptions.getElement().getStyle().setFontSize(site.properties.headerCaptionsFontSize, Unit.PX);
    }

    public void setHeaderCaptions(String captions) {
        headerCaptions.setText(captions);
        addToHeaderPanel(headerCaptions, site.properties.headerCaptionsLeft, site.properties.headerCaptionsTop);
    }

    protected void createLogoImage(String url) {
        logoImage = new Image();
        logoImage.setUrl(url);
        addToHeaderPanel(logoImage, 20, 20);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        Page page = site.getPage(event.getValue());
        if (page == null) {
            show(site.getHomePage());
            return;
        }
        show(page);
    }

    @Override
    public void onResize(ResizeEvent event) {

        Style style = getElement().getStyle();

        int winWidth = Window.getClientWidth();
        int winHeight = Window.getClientHeight();

        int mainPanelWidth = mainPanel.getOffsetWidth();
        int mainPanelHeight = mainPanel.getOffsetHeight() + headerPanel.getOffsetHeight() + footerPanel.getOffsetHeight();

        style.setWidth(Math.max(mainPanelWidth, winWidth), Unit.PX);
        style.setHeight(Math.max(mainPanelHeight, winHeight), Unit.PX);

    }

}
