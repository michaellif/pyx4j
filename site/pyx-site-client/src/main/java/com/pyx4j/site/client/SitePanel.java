/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import com.pyx4j.site.client.LinkBar.LinkBarType;
import com.pyx4j.site.client.NavigationBar.NavigationBarType;
import com.pyx4j.site.client.domain.Link;
import com.pyx4j.site.client.domain.Page;
import com.pyx4j.site.client.domain.Site;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.client.themes.dark.DarkTheme;
import com.pyx4j.site.client.themes.light.LightTheme;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.util.BrowserType;

public class SitePanel extends SimplePanel implements ValueChangeHandler<String> {

    private final Site site;

    private AbsolutePanel headerPanel;

    private FlowPanel mainPanel;

    private FlowPanel leftSectionPanel;

    private SimplePanel mainSectionPanel;

    private FlowPanel rightSectionPanel;

    private AbsolutePanel footerPanel;

    private Label headerCaptions;

    private Image logoImage;

    private NavigationBar primaryNavigationBar;

    private LinkBar headerLinkBar;

    private LinkBar footerLinkBar;

    private static LightTheme lightTheme = new LightTheme();

    private static DarkTheme darkTheme = new DarkTheme();

    public SitePanel(Site site) {
        this.site = site;

        setSize("100%", "100%");

        add(createContentPanel());
        setStyleName(SiteCSSClass.pyx4j_Site_SitePanel.name());

        createHeaderCaptions();

        createPrimaryNavigation();

        createLogoImage(site.logoUrl);

        createHeaderLinks(site.headerLinks);

        createFooterLinks(site.footerLinks);

        createFooterCopiright(site.footerCopiright);

        StyleManger.installTheme(lightTheme);

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

    private void show(Page page) {
        mainSectionPanel.setWidget(new HTML(page.data.html, true));

        setHeaderCaptions(page.caption);

        primaryNavigationBar.setSelected(page.name);

    }

    protected Panel createContentPanel() {
        FlowPanel contentPanel = new FlowPanel();

        Style style = contentPanel.getElement().getStyle();

        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");

        contentPanel.setStyleName(SiteCSSClass.pyx4j_Site_ContentPanel.name());

        headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);

        mainPanel = createMainPanel();
        contentPanel.add(mainPanel);

        leftSectionPanel = createLeftSectionPanel();
        mainPanel.add(leftSectionPanel);

        mainSectionPanel = createMainSectionPanel();
        mainPanel.add(mainSectionPanel);

        rightSectionPanel = createRightSectionPanel();
        mainPanel.add(rightSectionPanel);

        footerPanel = createFooterPanel();
        contentPanel.add(footerPanel);

        return contentPanel;
    }

    protected AbsolutePanel createHeaderPanel() {
        AbsolutePanel headerPanel = new AbsolutePanel();

        headerPanel.setStyleName(SiteCSSClass.pyx4j_Site_Header.name());

        return headerPanel;
    }

    public void addToHeaderPanel(Widget w) {
        addToHeaderPanel(w, 0, 0);
    }

    public void addToHeaderPanel(Widget w, int left, int top) {
        headerPanel.remove(w);
        headerPanel.add(w, left, top);
    }

    protected AbsolutePanel createFooterPanel() {
        AbsolutePanel footerPanel = new AbsolutePanel();

        footerPanel.setStyleName(SiteCSSClass.pyx4j_Site_Footer.name());

        return footerPanel;
    }

    public void addToFooterPanel(Widget w) {
        addToFooterPanel(w, 0, 0);
    }

    public void addToFooterPanel(Widget w, int left, int top) {
        footerPanel.add(w, left, top);
    }

    protected FlowPanel createMainPanel() {
        FlowPanel mainPanel = new FlowPanel();

        Style style = mainPanel.getElement().getStyle();

        style.setProperty("display", "inline-block");
        style.setProperty("width", "100%");

        mainPanel.setStyleName(SiteCSSClass.pyx4j_Site_MainPanel.name());

        return mainPanel;
    }

    protected FlowPanel createLeftSectionPanel() {
        FlowPanel panel = new FlowPanel();

        if (BrowserType.isFirefox()) {
            panel.getElement().getStyle().setProperty("cssFloat", "left");
        } else {
            panel.getElement().getStyle().setProperty("float", "left");
        }
        panel.getElement().getStyle().setProperty("display", "inline-block");

        panel.getElement().getStyle().setProperty("padding", "20px");
        panel.getElement().getStyle().setProperty("background", "red");

        HTML portlet1 = new HTML("portlet1", true);
        portlet1.setWidth("100px");
        panel.add(portlet1);
        HTML portlet2 = new HTML("portlet2", true);
        panel.add(portlet2);

        return panel;
    }

    protected FlowPanel createRightSectionPanel() {
        FlowPanel panel = new FlowPanel();
        if (BrowserType.isFirefox()) {
            panel.getElement().getStyle().setProperty("cssFloat", "right");
        } else {
            panel.getElement().getStyle().setProperty("float", "right");
        }
        panel.getElement().getStyle().setProperty("display", "inline-block");

        panel.getElement().getStyle().setProperty("padding", "20px");
        panel.getElement().getStyle().setProperty("background", "red");

        HTML portlet3 = new HTML("portlet3", true);
        portlet3.setWidth("100px");
        panel.add(portlet3);
        HTML portlet4 = new HTML("portlet4", true);
        panel.add(portlet4);

        return panel;
    }

    protected SimplePanel createMainSectionPanel() {
        SimplePanel panel = new SimplePanel();
        panel.getElement().getStyle().setProperty("display", "inline-block");
        panel.getElement().getStyle().setProperty("background", "green");
        return panel;
    }

    protected void createHeaderCaptions() {
        headerCaptions = new Label();
        headerCaptions.setStyleName(SiteCSSClass.pyx4j_Site_HeaderCaptions.name());

    }

    public void setHeaderCaptions(String captions) {
        headerCaptions.setText(captions);
        addToHeaderPanel(headerCaptions);
    }

    protected void createLogoImage(String url) {
        logoImage = new Image();
        logoImage.setStyleName(SiteCSSClass.pyx4j_Site_Logo.name());

        logoImage.setUrl(url);
        addToHeaderPanel(logoImage);
        logoImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (lightTheme.equals(StyleManger.getTheme())) {
                    StyleManger.installTheme(darkTheme);
                } else {
                    StyleManger.installTheme(lightTheme);
                }
            }
        });
    }

    protected void createPrimaryNavigation() {
        primaryNavigationBar = new NavigationBar(NavigationBarType.Primary);

        for (Page page : site.pages) {
            primaryNavigationBar.add(page.caption, page.name);
        }
        addToHeaderPanel(primaryNavigationBar);

    }

    protected void createFooterCopiright(String footerCopiright) {
        HTML html = new HTML(footerCopiright, false);
        html.setStyleName(SiteCSSClass.pyx4j_Site_FooterCopiright.name());
        addToFooterPanel(html);
    }

    protected void createFooterLinks(List<Link> footerLinks) {
        footerLinkBar = new LinkBar(LinkBarType.Footer);

        for (Link link : site.footerLinks) {
            footerLinkBar.add(link.html, link.href, link.internal);
        }
        addToFooterPanel(footerLinkBar);
    }

    protected void createHeaderLinks(List<Link> headerLinks) {
        headerLinkBar = new LinkBar(LinkBarType.Header);

        for (Link link : site.headerLinks) {
            headerLinkBar.add(link.html, link.href, link.internal);
        }
        addToHeaderPanel(headerLinkBar);
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

}
